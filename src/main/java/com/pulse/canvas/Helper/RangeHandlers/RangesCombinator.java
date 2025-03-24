package com.pulse.canvas.Helper.RangeHandlers;

import com.pulse.canvas.Dtoes.PixelUpdate;
import java.util.*;

public class RangesCombinator {

    // Merges updates considering older updates are not overridden by newer ones
    public static List<PixelUpdate> mergeUpdates(List<PixelUpdate> updates) {
        // Combine the existing and new updates into a single list A, sorted by start position
        List<PixelUpdate> A = new ArrayList<>(updates);

        // Sort by start position and then by timestamp (older updates first in case of overlap)
        A.sort(Comparator.comparingInt((PixelUpdate u) -> u.start).thenComparingInt(u -> u.timestamp));

        // This will store the final result of merged updates
        List<PixelUpdate> B = new ArrayList<>();

        // Iterate through the sorted list A
        for (int i = 0 ; i < A.size() ; i++) {
            PixelUpdate update = A.get(i);

            // If B is empty or there's no overlap, just add the update to B
            if (B.isEmpty()) {
                B.add(update);
                continue;
            }


            // Get the last update in B for overlap resolution
            PixelUpdate lastUpdate = B.get(B.size() - 1);

            // Check if the current update overlaps with the last one in B
            if (lastUpdate.end >= update.start) {
                // Overlap case: The last update has priority, handle based on timestamps
                if (update.timestamp < lastUpdate.timestamp) {

                     B.remove(lastUpdate);

                    // The current update is fully contained in the last one
                    if(lastUpdate.start < update.start){
                        B.add(new PixelUpdate(lastUpdate.start, update.start - 1, lastUpdate.timestamp)); // Add non-overlapping part of the last update

                    }else{
                        // TODO : reloop on same object
                        i--;
                        A.add(new PixelUpdate(lastUpdate.start,lastUpdate.end, lastUpdate.timestamp));
                        continue;
                    }
                    // Older update should be preserved, handle overlap
                    if (update.end <= lastUpdate.end) {

                        B.add(update); // Add the current update
                        B.add(new PixelUpdate(update.end + 1, lastUpdate.end, lastUpdate.timestamp)); // Add non-overlapping part of the last update
                    } else {

                        B.add(new PixelUpdate(update.start, update.end, update.timestamp));
                    }
                }
                else if(lastUpdate.timestamp == update.timestamp){
                    if(update.start <= lastUpdate.start && update.end >= lastUpdate.end){
                        B.remove(lastUpdate);
                        B.add(update);
                    }
                    else if(update.start <= lastUpdate.start){
                        B.remove(lastUpdate);
                        B.add(new PixelUpdate(update.start , lastUpdate.start - 1,update.timestamp));
                        B.add(lastUpdate);
                    }
                }
                else {
                   if(update.start < lastUpdate.start){
                       B.add(new PixelUpdate(update.start , lastUpdate.start - 1,update.timestamp));
                   }
                   if(update.end > lastUpdate.end){
                       B.add(new PixelUpdate(lastUpdate.end+1,update.end,update.timestamp));

                   }
                }
            } else {
                // No overlap, just add the update to B
                B.add(update);
            }
        }

        return B;
    }

    public static void main(String[] args) {
        testEdgeCases();
    }

    public static void testEdgeCases() {
        // Test case 1: New update should not override older ones
        testCase(
                "New update should not override older ones",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(5, 15, 2000))
        );

        // Test case 2: No overlap, new update is completely after the existing update
        testCase(
                "No overlap, new update is after existing",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(21, 30, 2000))
        );

        // Test case 3: No overlap, new update is completely before the existing update
        testCase(
                "No overlap, new update is before existing",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(0, 5, 2000))
        );

        // Test case 4: Full overlap, new update entirely covers the existing update
        testCase(
                "Full overlap, new update fully covers existing",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(5, 25, 2000))
        );

        // Test case 5: Exact overlap, same range with different timestamps
        testCase(
                "Exact overlap, same range with different timestamps",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(10, 20, 2000))
        );

        // Test case 6: Multiple new updates, none of them overlap
        testCase(
                "Multiple new updates, none overlap",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(
                        new PixelUpdate(0, 5, 1000),
                        new PixelUpdate(21, 30, 1500)
                )
        );

        // Test case 7: Multiple updates, overlapping updates with timestamps
        testCase(
                "Multiple overlapping updates with different timestamps",
                Arrays.asList(
                        new PixelUpdate(10, 20, 500),
                        new PixelUpdate(30, 40, 800)
                ),
                Arrays.asList(
                        new PixelUpdate(15, 25, 1000),
                        new PixelUpdate(35, 45, 1500)
                )
        );

        // Test case 8: Single new update fully contained within an existing one
        testCase(
                "New update fully contained within existing update",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(12, 18, 1000))
        );

        // Test case 9: Single new update with the same start and end as existing
        testCase(
                "Single new update with the same start and end as existing",
                Arrays.asList(new PixelUpdate(10, 20, 500)),
                Arrays.asList(new PixelUpdate(10, 20, 2000))
        );

        // Test case 10: Updates with non-overlapping ranges, mixed timestamps
        testCase(
                "Updates with non-overlapping ranges, mixed timestamps",
                Arrays.asList(
                        new PixelUpdate(5, 10, 300),
                        new PixelUpdate(20, 30, 600)
                ),
                Arrays.asList(
                        new PixelUpdate(10, 15, 1000),
                        new PixelUpdate(15, 20, 1500)
                )
        );

        // Test case 11: Updates with gaps between them
        testCase(
                "Updates with gaps between them",
                Arrays.asList(
                        new PixelUpdate(0, 5, 100),
                        new PixelUpdate(15, 25, 500)
                ),
                Arrays.asList(
                        new PixelUpdate(5, 100, 2000),
                        new PixelUpdate(20, 30, 1500)
                )
        );

        // Test case 12: Existing updates have the same start, but different ends
        testCase(
                "Existing updates have the same start, but different ends",
                Arrays.asList(
                        new PixelUpdate(5, 10, 500),
                        new PixelUpdate(5, 15, 1000)
                ),
                Arrays.asList(new PixelUpdate(15, 20, 1500))
        );

        // Test case 13: Both existing and new updates are in reverse order of timestamps
        testCase(
                "Both existing and new updates are in reverse order of timestamps",
                Arrays.asList(
                        new PixelUpdate(10, 20, 1500),
                        new PixelUpdate(30, 40, 1000)
                ),
                Arrays.asList(
                        new PixelUpdate(5, 10, 500),
                        new PixelUpdate(20, 30, 2000)
                )
        );
    }


    private static void testCase(String name, List<PixelUpdate> existing, List<PixelUpdate> newUpdates) {
        System.out.println("\n--- Test: " + name + " ---");
        System.out.println("Existing updates:");
        existing.forEach(System.out::println);
        System.out.println("New updates:");
        newUpdates.forEach(System.out::println);

        List<PixelUpdate> modifiableExisting = new ArrayList<>(existing);

        modifiableExisting.addAll(newUpdates); // Add new updates
        List<PixelUpdate> result = mergeUpdates(modifiableExisting); // Merge updates

        System.out.println("Result:");
        result.forEach(System.out::println);
        System.out.println("------------------------");
    }
}
