package com.pulse.canvas.Repositories;


import com.pulse.canvas.entities.CanvasDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanvasDocRepository extends MongoRepository<CanvasDoc,String> {

}
