package vttp2022.paf.assessment.eshop.respositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Repository
public class OrderRepository {
	// TODO: Task 3

	@Autowired
	private JdbcTemplate jTemplate;

	private Integer insertOrder(Order o) {
		return jTemplate.update(SQL_INSERT_ORDER, o.getOrderId(), o.defaultValue(o.getDeliveryId(), "Processing"),
			o.getName(), o.getAddress(), o.getEmail(), o.defaultValue(o.getStatus(), "Pending"), o.getOrderDate());
	}

	private int[] insertLineItems(List<LineItem> lineItems, String orderId) {
		List<Object[]> data = lineItems.stream()
                .map(li -> {
                    Object[] l = new Object[3];
                    l[0] = orderId;
                    l[1] = li.getItem();
                    l[2] = li.getQuantity();
                    return l;
                }).toList();

        return jTemplate.batchUpdate(SQL_INSERT_LINE_ITEMS, data);
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean insertFullOrder(Order o) {
		Integer inserted = insertOrder(o);
		int[] insertedLineItems = insertLineItems(o.getLineItems(), o.getOrderId());
		if(inserted > 0 && insertedLineItems.length > 0)
			return true;
		return false;
	}

	public Integer insertOrderStatus(OrderStatus os) {
		return jTemplate.update(SQL_INSERT_ORDER_STATUS, os.getDeliveryId(), os.getStatus(), new Date());
	}

	public Integer updateOrder(OrderStatus os, String order_id) {
		return jTemplate.update(SQL_UPDATE_ORDER_WITH_STATUS, os.getDeliveryId(), os.getStatus(), order_id);
	}

	public List<Integer> checkStatus(String name) {
		List<Integer> countList = new LinkedList<>();
		Integer total = 0;
		SqlRowSet rs = jTemplate.queryForRowSet(SQL_FIND_COUNT_OF_ORDERS, name);
		if(rs.next())
			total = (rs.getInt("total"));
		rs = jTemplate.queryForRowSet(SQL_FIND_COUNT_OF_ORDERS_DISPATCHED, name, "dispatched");
		if(rs.next())
			countList.add(rs.getInt("dispatched"));
		countList.add(total - rs.getInt("dispatched"));

		return countList;
	}
}
