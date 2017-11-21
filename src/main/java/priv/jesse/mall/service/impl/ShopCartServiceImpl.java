package priv.jesse.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import priv.jesse.mall.entity.OrderItem;
import priv.jesse.mall.entity.Product;
import priv.jesse.mall.entity.User;
import priv.jesse.mall.service.ProductService;
import priv.jesse.mall.service.ShopCartService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author hfb
 * @date 2017/11/21
 */
public class ShopCartServiceImpl implements ShopCartService {

    @Autowired
    private ProductService productService;

    /**
     * 加购物车
     *
     * @param productId
     * @param request
     */
    @Override
    public void addCart(int productId, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("login_user");
        List<Integer> productIds = (List<Integer>) request.getSession().getAttribute(NAME_PREFIX + loginUser.getId());
        if (productIds == null) {
            productIds = new ArrayList<>();
            request.getSession().setAttribute(NAME_PREFIX + loginUser.getId(), productIds);
        }
        productIds.add(productId);
    }

    /**
     * 移除
     *
     * @param productId
     * @param request
     */
    @Override
    public void remove(int productId, HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("login_user");
        List<Integer> productIds = (List<Integer>) request.getSession().getAttribute(NAME_PREFIX + loginUser.getId());
        Iterator<Integer> iterator = productIds.iterator();
        while (iterator.hasNext()) {
            if (productId == iterator.next()) {
                iterator.remove();
            }
        }
    }

    /**
     * 查看购物车
     *
     * @param request
     * @return
     */
    @Override
    public List<OrderItem> listCart(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute("login_user");
        List<Integer> productIds = (List<Integer>) request.getSession().getAttribute(NAME_PREFIX + loginUser.getId());
        Map<Integer, OrderItem> productMap = new HashMap<>();
        for (Integer productId : productIds) {
            if (productMap.get(productId) == null) {
                Product product = productService.findById(productId);
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setProductId(productId);
                orderItem.setCount(1);
                orderItem.setSubTotal(product.getShopPrice());
                productMap.put(productId, orderItem);
            } else {
                OrderItem orderItem = productMap.get(productId);
                int count = orderItem.getCount();
                orderItem.setCount(++count);
                Double subTotal = orderItem.getSubTotal();
                orderItem.setSubTotal(subTotal * 2);
                productMap.put(productId, orderItem);
            }
        }
        List<OrderItem> orderItems = new ArrayList<>(productMap.values());
        return orderItems;
    }
}
