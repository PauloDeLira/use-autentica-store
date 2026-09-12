export interface WhatsAppTopProduct {
  productId: string;
  productName: string;
  clicks: number;
}

export interface WhatsAppClickStats {
  clicksToday: number;
  clicksThisWeek: number;
  topProducts: WhatsAppTopProduct[];
}
