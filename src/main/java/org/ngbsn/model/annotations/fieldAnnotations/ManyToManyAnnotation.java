package org.ngbsn.model.annotations.fieldAnnotations;

import lombok.Builder;
import org.ngbsn.model.annotations.Annotation;

@Builder
public class ManyToManyAnnotation implements Annotation {

    private String mappedBy;

    @Override
    public String toString() {

        return mappedBy == null ? "@ManyToMany" : "@ManyToMany(mappedBy = \"" + mappedBy + "\")";
    }
}
