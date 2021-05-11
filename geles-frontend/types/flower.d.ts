interface Flower {
  id: number;
  name: string;
  price: number;
  photo?: string;
  description: string;
  daysToExpire: number;
  favorite: boolean;
}

interface FlowerInCart {
  id: number;
  amount: number;
  flowerId: number;
  cartId: number;
  sum: number;
  price: number;
  name: string;
}
