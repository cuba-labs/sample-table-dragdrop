package com.company.sales.web.order;

import com.company.sales.entity.OrderLine;
import com.company.sales.entity.Product;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionChangeType;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import com.company.sales.entity.Order;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;

import javax.inject.Inject;
import java.math.BigDecimal;

@UiController("sales_Order.edit")
@UiDescriptor("order-edit.xml")
@EditedEntityContainer("orderDc")
@LoadDataBeforeShow
public class OrderEdit extends StandardEditor<Order> {

    @Inject
    private Table<Product> productsTable;

    @Inject
    private Table<OrderLine> linesTable;

    @Inject
    private ScreenBuilders screenBuilders;

    @Inject
    private Metadata metadata;

    @Inject
    private CollectionContainer<OrderLine> linesDc;

    @Inject
    private CollectionContainer<Product> productsDc;

    @Subscribe
    public void onInit(InitEvent event) {
        productsTable.withUnwrapped(com.vaadin.v7.ui.Table.class, table ->
                table.setDragMode(com.vaadin.v7.ui.Table.TableDragMode.ROW));

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
    }

    @Subscribe(id = "linesDc", target = Target.DATA_CONTAINER)
    protected void onOrderLinesDcCollectionChange(CollectionContainer.CollectionChangeEvent<OrderLine> event) {
        if (event.getChangeType() != CollectionChangeType.REFRESH) {
            calculateAmount();
        }
    }

    protected void calculateAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        for (OrderLine line : linesDc.getItems()) {
            amount = amount.add(line.getProduct().getPrice().multiply(line.getQuantity()));
        }
        getEditedEntity().setAmount(amount);
    }
}