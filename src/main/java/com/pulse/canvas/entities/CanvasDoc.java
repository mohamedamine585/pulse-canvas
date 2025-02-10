package com.pulse.canvas.entities;


import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collation = "canvasDocs")
public class CanvasDoc {
    @Id
   String id;
   String name;
   String creatorID;
   short[] print;
   @CreatedDate()
   Date created;
   @LastModifiedDate()
    Date modified;

}
