package com.pulse.canvas.services;


import com.pulse.canvas.Helper.RGBAUtils;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.entities.CanvasPrint;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DatabaseService {

    @Autowired
    private CanvasPrintRepository canvasPrintRepository;

    @Transactional
    public void updateDataBase(Long canvasId, int biggestPos, List<Long> updatedPixelPositions, List<Long> updatedPixelEdits) {
        CanvasPrint canvasPrint = canvasPrintRepository.findByCanvasId(canvasId);
        if (canvasPrint == null) {
            throw new EntityNotFoundException("CanvasPrint not found with canvasId: " + canvasId);
        }

        byte[] storedPrint = canvasPrint.getPrint();
        byte[] newPrint = new byte[(biggestPos + 1) * 4];

        if (storedPrint.length > newPrint.length) {
            canvasPrint.setPrint(buildPrintByteArray(updatedPixelPositions, updatedPixelEdits, storedPrint));
        } else {
            System.arraycopy(storedPrint, 0, newPrint, 0, storedPrint.length);
            canvasPrint.setPrint(buildPrintByteArray(updatedPixelPositions, updatedPixelEdits, newPrint));
        }
        canvasPrintRepository.save(canvasPrint);
    }

    private byte[] buildPrintByteArray(List<Long> updatedPixelPositions, List<Long> updatedPixelEdits, byte[] byteArray) {
        for (int i = 0; i < updatedPixelPositions.size(); i++) {
            try {
                byte[] rgba = RGBAUtils.decodeRGBA(updatedPixelEdits.get(i));
                int position = Math.toIntExact(updatedPixelPositions.get(i)) * 4;
                if (position + 3 < byteArray.length) {
                    byteArray[position] = rgba[0];
                    byteArray[position + 1] = rgba[1];
                    byteArray[position + 2] = rgba[2];
                    byteArray[position + 3] = rgba[3];
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return byteArray;
    }
}