package models;

import yalp.db.jpa.*;
import yalp.data.validation.*;

import javax.persistence.*;

@Entity
public class Category extends Model {

    @Required
    public String label;
    
    @Required
    public String code;

    public String toString() {
        return label;
    }
}

