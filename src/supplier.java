import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class supplier implements ActionListener {
    JFrame frame;
    fileParser fileParser = new fileParser();
    public JPanel stockPanel = new JPanel();
    public JPanel orderControlPanel = new JPanel();

    JScrollPane scrollStockFrame = new JScrollPane(stockPanel);
    JScrollPane scrollOrderControlFrame = new JScrollPane(orderControlPanel);

    supplier(JFrame frame){
        this.frame = frame;
    }

    /**
     * Called when a button is clicked. Events include, but are not limited to, moving orders to ready/shipped, view stock,
     *  view orders, etc...
     * @param e : param holding event info
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String[] actionCommand = ((JButton) e.getSource()).getActionCommand().split(",");

        switch (actionCommand[0]) {
            case "View Orders":
                try {
                    switchToOrderControlPanel();
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "View Stock":
                try {
                    switchToStockPanel();
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "Move to ready":
                try {
                    moveOrderToReady(actionCommand[1]);
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "Move to shipped":
                try {
                    moveOrderToShipped(actionCommand[1]);
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
                break;
        }
    }

    //Panel constructs

    /**
     * Cosntructs the frame and all of the subpanels
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void frame() throws IOException, ParseException {
        frame.setSize(450,900);
        frame.setTitle("Current Stock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        stockFrame();
        orderControlFrame();

        frame.setContentPane(scrollStockFrame);
        stockPanel.setVisible(true);
        frame.setVisible(true);
    }

    /**
     * Creates the panel responsible for displaying the current stock (parsed from the ../config_files/items.json field
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void stockFrame() throws IOException, ParseException {
        int y_level = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        stockPanel.setLayout(new GridBagLayout());
        stockPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        orderControlFrame();

        addViewOrdersButton(gbc, y_level); y_level++;

        //For each item currently available for purchase add a row detailing the name, price, and give a button to addToCart
        addStockHeaders(gbc, y_level); y_level+=2;
        ArrayList<item> items = fileParser.getItems();
        for (item item : items) {
            addStockRow(gbc, item, y_level);
            y_level++;
        }

        addEmptyLabel(gbc, y_level, stockPanel); y_level++;
    }

    /**
     * Constructs the panel responsible for processing orders (i.e. marking orders as 'Ready' or 'Shipped'
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void orderControlFrame() throws IOException, ParseException {
        int y_level = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        orderControlPanel.setLayout(new GridBagLayout());
        orderControlPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        addViewStockButton(gbc, y_level); y_level++;

        ArrayList<order> orders = fileParser.getOrders();
        for(order order : orders){
            addOrderDescription(gbc, y_level, order);
            y_level+=7;
            for(item item : order.getItems()){
                for(int i = 0; i < item.getQuantityPurchased(); i++){
                    addProductPurchased(gbc, item, y_level);
                    y_level++;
                }
            }

            addOrderTotal(gbc, y_level, order.getOrderTotal());
            y_level++;
            addOrderControlButtons(gbc, y_level, order.getOrderId());
            y_level++;
            addFillerRow(gbc, y_level, "..................................................................................");
            y_level++;
        }

        addEmptyLabel(gbc, y_level, orderControlPanel); y_level++;
    }

    //Order Control Methods

    /**
     * This method checks that there is enough stock of all items bought to mark an order as 'Ready' and if so, it updates
     *  the order status. Else notifies the Supplier to refill inventory.
     * @param orderID : unique ID of the order
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void moveOrderToReady(String orderID) throws IOException, ParseException {
        String previousState = null;
        String desiredState = "Ready";
        order currentOrder = null;

        //get the previous state of the order (i.e. was it previously 'Ordered' or 'ready'
        ArrayList<order> orders = fileParser.getOrders();
        for(order order : orders){
            if(order.getOrderId().equals(orderID)){
                previousState = order.getOrderStatus();
                currentOrder = order;
                break;
            }
        }

        //If coming from an 'Ordered' status make sure there is enough available stock for all items purchased, and if there is
        //then subtract the quantityPurchased from the available stock for all items
        if(previousState.equals("Ordered")){
            for(item item : currentOrder.getItems()){
                if(!fileParser.changeItemAvailableAmount(item.getName(), -1*item.getQuantityPurchased(), false)) {
                    JOptionPane.showConfirmDialog(null, "Not enough available stock for " + item.getName(), "Not Enough Stock", JOptionPane.OK_CANCEL_OPTION);
                    return;
                }
            }

            for(item item : currentOrder.getItems()){
                fileParser.changeItemAvailableAmount(item.getName(), -1*item.getQuantityPurchased(), true);
                fileParser.changeItemReservedAmount(item.getName(), item.getQuantityPurchased(), true);
            }
        }

        //If coming from an 'Ready' status make sure there is enough reserved stock for all items purchased, and if there is
        //then subtract the quantityPurchased from the reserved stock for all items
        else if(previousState.equals("Shipped")){
            for(item item : currentOrder.getItems())
                fileParser.changeItemReservedAmount(item.getName(), item.getQuantityPurchased(), true);

        }

        //if there was enough stock for the order status change, update the order status and refresh the panel to reflect this change
        fileParser.updateOrderStatus(orderID, desiredState);
        switchToStockPanel();
        switchToOrderControlPanel();
    }

    /**
     * This method checks that there is enough stock of all items bought to mark an order as 'Shipped' and if so, it updates
     *  the order status. Else notifies the Supplier to refill inventory.
     * @param orderID : unique ID of the order
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void moveOrderToShipped(String orderID) throws IOException, ParseException {
        String previousState = null;
        String desiredState = "Shipped";
        order currentOrder = null;

        //get the previous state of the order (i.e. was it previously 'Ordered' or 'ready'
        ArrayList<order> orders = fileParser.getOrders();
        for(order order : orders){
            if(order.getOrderId().equals(orderID)){
                previousState = order.getOrderStatus();
                currentOrder = order;
                break;
            }
        }

        //If coming from an 'Ordered' status make sure there is enough available stock for all items purchased, and if there is
         //then subtract the quantityPurchased from the available stock for all items
        if(previousState.equals("Ordered")){
            for(item item : currentOrder.getItems()){
                if(!fileParser.changeItemAvailableAmount(item.getName(), -1*item.getQuantityPurchased(), false)) {
                    JOptionPane.showConfirmDialog(null, "Not enough available stock for " + item.getName(), "Not Enough Stock", JOptionPane.OK_CANCEL_OPTION);
                    return;
                }
            }

            for(item item : currentOrder.getItems()){
                fileParser.changeItemAvailableAmount(item.getName(), -1*item.getQuantityPurchased(), true);
            }
        }

        //If coming from an 'Ready' status make sure there is enough reserved stock for all items purchased, and if there is
        //then subtract the quantityPurchased from the reserved stock for all items
        else if(previousState.equals("Ready")){
            for(item item : currentOrder.getItems()){
                if(!fileParser.changeItemReservedAmount(item.getName(), -1*item.getQuantityPurchased(), false)) {
                    JOptionPane.showConfirmDialog(null, "Not enough reserved stock for " + item.getName(), "Not Enough Stock", JOptionPane.OK_CANCEL_OPTION);
                    return;
                }
            }

            for(item item : currentOrder.getItems())
                fileParser.changeItemReservedAmount(item.getName(), -1*item.getQuantityPurchased(), true);

        }

        //if there was enough stock for the order status change, update the order status and refresh the panel to reflect this change
        fileParser.updateOrderStatus(orderID, desiredState);
        switchToStockPanel();
        switchToOrderControlPanel();
    }

    //Panel switching methods
    public void switchToOrderControlPanel() throws IOException, ParseException {
        orderControlPanel = new JPanel();
        orderControlFrame();
        scrollOrderControlFrame = new JScrollPane(orderControlPanel);

        frame.setContentPane(scrollOrderControlFrame);
        frame.setTitle("Orders");
        frame.setSize(450,901);
    }

    public void switchToStockPanel() throws IOException, ParseException {
        stockPanel = new JPanel();
        stockFrame();
        scrollStockFrame = new JScrollPane(stockPanel);

        frame.setContentPane(scrollStockFrame);
        frame.setTitle("Current Stock");
        frame.setSize(450,900);
    }

    //Panel Components
    public void addViewOrdersButton(GridBagConstraints gbc, int y_level){
        JButton viewOrders = new JButton("View Orders");
        viewOrders.setActionCommand("View Orders");
        viewOrders.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        stockPanel.add(viewOrders, gbc);
    }

    public void addViewStockButton(GridBagConstraints gbc, int y_level){
        JButton viewOrders = new JButton("View Stock");
        viewOrders.setActionCommand("View Stock");
        viewOrders.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        orderControlPanel.add(viewOrders, gbc);
    }

    public void addEmptyLabel(GridBagConstraints gbc, int y_level, JPanel panel){
        JLabel empty = new JLabel();
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.gridy = y_level;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(empty, gbc);
        gbc.gridwidth = 1;
    }

    public void addStockRow(GridBagConstraints gbc, item item, int y_level){
        JLabel name = new JLabel(item.getName());
        JLabel price = new JLabel("$" + (item.getPrice()/100.0));
        JLabel reserved = new JLabel("" + item.getReserved());
        JLabel available = new JLabel("" + item.getAvailable());

        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        stockPanel.add(name, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        stockPanel.add(price, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        stockPanel.add(reserved, gbc);

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        stockPanel.add(available, gbc);
    }

    public void addStockHeaders(GridBagConstraints gbc, int y_level){
        JLabel name = new JLabel("Name");
        JLabel price = new JLabel("Price");
        JLabel reserved = new JLabel("Reserved");
        JLabel available = new JLabel("Available");
        JLabel divider = new JLabel(".................................................................................................................................");

        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        stockPanel.add(name, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        stockPanel.add(price, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        stockPanel.add(reserved, gbc);

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        stockPanel.add(available, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+1;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        stockPanel.add(divider, gbc);
        gbc.gridwidth = 1;
    }

    public void addOrderDescription(GridBagConstraints gbc, int y_level, order order){
        JLabel orderID = new JLabel("Order ID:");
        JLabel customerID = new JLabel("Customer ID:");
        JLabel status = new JLabel("Status:");
        JLabel date = new JLabel("Date:");
        JLabel shippingMethod = new JLabel("Delivery Method:");
        JLabel purchaseAuthorizationNumber = new JLabel("Confirmation #:");
        JLabel items = new JLabel("Items Purchased :");
        JLabel orderField = new JLabel(order.getOrderId());
        JLabel customerIDField = new JLabel(order.getCustomerId());
        JLabel statusField = new JLabel(order.getOrderStatus());
        JLabel dateField = new JLabel(order.getOrderDate());
        JLabel shippingField = new JLabel(order.getShippingMethod());
        JLabel purchaseAuthorizationNumberField = new JLabel(order.getPurchaseAuthorizationNumber());


        gbc.gridx = 0;
        gbc.gridy = y_level;
        orderControlPanel.add(orderID, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(orderField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+1;
        orderControlPanel.add(customerID, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(customerIDField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+2;
        orderControlPanel.add(status, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(statusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+3;
        orderControlPanel.add(date, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+4;
        orderControlPanel.add(shippingMethod, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(shippingField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+5;
        orderControlPanel.add(purchaseAuthorizationNumber, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(purchaseAuthorizationNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+6;
        orderControlPanel.add(items, gbc);

    }

    public void addProductPurchased(GridBagConstraints gbc, item item, int y_level){
        //Img   Name   Price   Add to cart
        JLabel name = new JLabel("    " + item.getName());
        JLabel price = new JLabel("    $" + (item.getPrice()/100.0));

        gbc.gridx = 0;
        gbc.gridy = y_level;
        orderControlPanel.add(name, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(price, gbc);

    }

    public void addOrderTotal(GridBagConstraints gbc, int y_level, int total){
        JLabel orderTotal = new JLabel("Total :");
        JLabel orderTotalField = new JLabel("$" + (total/100.0));

        gbc.gridx = 0;
        gbc.gridy = y_level;
        orderControlPanel.add(orderTotal, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(orderTotalField, gbc);
    }

    public void addFillerRow(GridBagConstraints gbc, int y_level, String content){
        JLabel empty = new JLabel(content);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = y_level;
        orderControlPanel.add(empty, gbc);
        gbc.gridwidth = 1;
    }

    public void addOrderControlButtons(GridBagConstraints gbc, int y_level, String orderID){
        JButton moveToReady = new JButton("Mark as Ready");
        JButton moveToShipped = new JButton("Mark as Shipped");
        moveToReady.setActionCommand("Move to ready," + orderID);
        moveToShipped.setActionCommand("Move to shipped," + orderID);
        moveToReady.addActionListener(this);
        moveToShipped.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = y_level;
        orderControlPanel.add(moveToReady, gbc);

        gbc.gridx = 1;
        orderControlPanel.add(moveToShipped, gbc);

    }
}
