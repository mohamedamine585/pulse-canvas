package com.pulse.canvas.Helper.RangeHandlers;

import java.util.ArrayList;
import java.util.List;

public class RangeExtractor {
    public static List<int[]> extractIndexes(int[] arr) {
        List<int[]> result = new ArrayList<>();
        int start = -1;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                if (start == -1) start = i; // Start of a sequence
            } else if (start != -1) {
                // If it's a single element range, add only the start
                if (start == i - 1) {
                    result.add(new int[]{start});
                } else {
                    result.add(new int[]{start, i - 1}); // Store (start, end) index for sequences with more than 1 element
                }
                start = -1;
            }
        }

        // Handle the case where the last sequence reaches the end of the array
        if (start != -1) {
            if (start == arr.length - 1) {
                result.add(new int[]{start}); // Single element at the end
            } else {
                result.add(new int[]{start, arr.length - 1}); // Range for multi-element sequence
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] arr = {0, 1, 2, 3, 0, 0, 0, 4};
        List<int[]> sequences = extractIndexes(arr);

        for (int[] indexes : sequences) {
            System.out.print("[");
            for (int i = 0; i < indexes.length; i++) {
                System.out.print(indexes[i]);
                if (i < indexes.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
}
