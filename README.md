# Table Drag and Drop feature  

This sample demonstrates how to implement dragging rows from one table to another.

Here we extend [CUBA Sales](https://github.com/cuba-platform/sample-sales-cuba7) application.

In `OrderEdit` screen we can drag entity row from Product table and drop it to the `Lines` table.
To enable dragging items from the table we should set drag mode:
 
```
productsTable.withUnwrapped(com.vaadin.v7.ui.Table.class, table ->
        table.setDragMode(com.vaadin.v7.ui.Table.TableDragMode.ROW));
```

To handle drop event in the table we should set `DropHandler`.

```
linesTable.withUnwrapped(com.vaadin.v7.ui.Table.class, table ->
        table.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                Object productId = event.getTransferable().getData("itemId");
                Product product = productsDc.getItem(productId);

                OrderLine lineWithProduct = metadata.create(OrderLine.class);
                lineWithProduct.setProduct(product);

                screenBuilders.editor(linesTable)
                        .withOpenMode(OpenMode.DIALOG)
                        .newEntity(lineWithProduct)
                        .show();
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }
        }));
```
