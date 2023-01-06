package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    
    public static final String SQL_FIND_CUSTOMER_BY_NAME = "select * from customers where name = ?";

    public static final String SQL_INSERT_ORDER = "insert into orders(order_id, delivery_id, name, address, email, status, orderDate) values (?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_INSERT_LINE_ITEMS = "insert into lineItems(order_id, item, quantity) values (?, ?, ?)";

    public static final String SQL_INSERT_ORDER_STATUS = "insert into order_status(delivery_id, status, status_update) values (?, ?, ?)";

    public static final String SQL_UPDATE_ORDER_WITH_STATUS = 
        "update orders set delivery_id = ?, status = ? where order_id = ?";

    public static final String SQL_FIND_COUNT_OF_ORDERS =
        "select count(*) as total from orders left join order_status on orders.delivery_id = order_status.delivery_id where name = ?";
    public static final String SQL_FIND_COUNT_OF_ORDERS_DISPATCHED =
        "select count(*) as dispatched from orders left join order_status on orders.delivery_id = order_status.delivery_id where name = ? and order_status.status = ? ";
}
