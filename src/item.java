class item{
    String id; //id of the product
    String name; //name of the product
    int price; //price of the product * 100 (no decimal place. $1.99 would be 199)
    int reserved; //units reserved
    int available; //units available
    String imgName; //file path of the img (id applicable)
    int quantityPurchased = 0; //holds the number of items purchased for order history

    //constructors
    item(){
    }

    item(String id, String name, int price, int reserved, int available){
        this.id = id;
        this.name = name;
        this.price = price;
        this.reserved = reserved;
        this.available = available;
    }

    item(String id, String name, int price, int reserved, int available, String imgName){
        this.id = id;
        this.name = name;
        this.price = price;
        this.reserved = reserved;
        this.available = available;
        this.imgName = imgName;
    }

    item(String id, String name, int price, String imgName, int quantityPurchased){
        this.id = id;
        this.name = name;
        this.price = price;
        this.imgName = imgName;
        this.quantityPurchased = quantityPurchased;
    }

    //Get methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailable() {
        return available;
    }

    public int getQuantityPurchased(){ return quantityPurchased; }

    public String getImgName() {
        return imgName;
    }

    public void setQuantityPurchased(int quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }
}
