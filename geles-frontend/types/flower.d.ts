interface Flower {
  id: number;
  name: string;
  price: number;
  photo?: string;
  description: string;
  daysToExpire: number;
  favorite: boolean;
}

interface Cart {
  id: number;
  flowersInCart: FlowerInCart[];
}

interface FlowerInCart {
  id: number;
  amount: number;
  flowerId: number;
  cartId: number;
  sum: number;
  price: number;
  name: string;
  photo: string;
}

enum OrderStatus {
  UNPAID = "UNPAID",
  PAID = "PAID",
  CONFIRMED = "CONFIRMED",
  DELIVERED = "DELIVERED",
  CANCELED = "CANCELED"
}

interface Order {
  readonly id: number;
  readonly createdDate: string;
  readonly totalOrderPrice: number;
  address: string;
  contactPhone: string;
  orderFlowers: OrderFlower[];
  readonly userId: number;
  orderStatus: OrderStatus;
}

interface OrderAdd {
  address: string;
  contactPhone: string;
  cartId: number;
}

interface OrderFlower {
  readonly id: number;
  quantity: number;
  flowerId: number;
}
