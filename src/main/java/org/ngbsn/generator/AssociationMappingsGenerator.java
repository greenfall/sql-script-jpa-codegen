package org.ngbsn.generator;

import org.ngbsn.model.Column;
import org.ngbsn.model.ForeignKeyConstraint;
import org.ngbsn.model.Table;

import java.util.List;
import java.util.Optional;

import static org.ngbsn.generator.BiDirectionalMappingsGenerator.addBiDirectionalMappings;
import static org.ngbsn.generator.UniDirectionalMappingsGenerator.addBothSideUniDirectionalMappings;

/**
 * This class contains logic for generating all the association mappings
 */
public class AssociationMappingsGenerator {

    public static void generateMappings(final Table table) {

        List<ForeignKeyConstraint> foreignKeyConstraintList = table.getForeignKeyConstraints();
        if (foreignKeyConstraintList.size() == 1) {
            //Case: There is only 1 Foreign key or 1 Composite Foreign Key
            //Treat this as a regular table and add a new column with for parent field with @ManyToOne
            //and add a new column in ReferencedTable for child field with @OneToMany
            addBothSideUniDirectionalMappings(table, foreignKeyConstraintList.get(0));

        } else {
            //Case: There are multiple Foreign Keys or multiple Composite Foreign Keys
            //Treat this as a Link table
            List<String> foreignKeyColumns = foreignKeyConstraintList.stream().flatMap(foreignKeyConstraint -> foreignKeyConstraint.getColumns().stream()).toList();
            Optional<Column> optionalKey = table.getColumns().stream().filter(column -> !foreignKeyColumns.contains(column.getColumnName())).findAny();
            if (optionalKey.isEmpty() && foreignKeyConstraintList.size() == 2) {
                //Case: All fields are foreign keys. Also, the relation exits between 2 entities only
                //Remove this link entity from the tablesMap as separate entity is not needed to track Link Table. Use @ManyToMany on other 2 entities
                addBiDirectionalMappings(table, foreignKeyConstraintList);

            } else {
                //Case1: There are some fields that are not foreign keys. So separate entity is needed to track Link Table
                //Case2: All fields are foreign keys. But, the relation exits between 2 or more entities
                //Add @ManyToOne for each foreignKey and corresponding @OneToMany in referenced Table
                foreignKeyConstraintList.forEach(foreignKeyConstraint -> {
                    addBothSideUniDirectionalMappings(table, foreignKeyConstraint);
                });
            }
        }

    }
}
