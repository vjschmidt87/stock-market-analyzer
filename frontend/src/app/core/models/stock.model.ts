export interface Stock {
  symbol: string;
  companyName: string;
  sector: string;
  industry: string;
  exchange: string;
  description: string;
  currentPrice: number;
  previousClose: number;
  changeAmount: number;
  changePercent: number;
  dayHigh: number;
  dayLow: number;
  weekHigh52: number;
  weekLow52: number;
  volume: number;
  avgVolume: number;
  marketCap: number;
  peRatio: number;
  dividendYield: number;
  lastUpdated: string;
}

export interface StockSnapshot {
  date: string;
  openPrice: number;
  highPrice: number;
  lowPrice: number;
  closePrice: number;
  volume: number;
}

export interface AnalysisResult {
  stockSymbol: string;
  companyName: string;
  recommendation: string;
  overallScore: number;
  sma20: number;
  sma50: number;
  sma200: number;
  ema12: number;
  ema26: number;
  rsi: number;
  macdLine: number;
  macdSignal: number;
  macdHistogram: number;
  bollingerUpper: number;
  bollingerMiddle: number;
  bollingerLower: number;
  volumeChange: number;
  summary: string;
  analyzedAt: string;
}

export interface MarketStats {
  totalStocks: number;
  gainers: number;
  losers: number;
  totalMarketCap: number;
  topGainers: Stock[];
  topLosers: Stock[];
}

export interface Watchlist {
  id: number;
  name: string;
  stocks: WatchlistStock[];
  createdAt: string;
}

export interface WatchlistStock {
  symbol: string;
  companyName: string;
  currentPrice: number;
  changePercent: number;
  position: number;
  addedAt: string;
}

export interface Alert {
  id: number;
  stockSymbol: string;
  companyName: string;
  alertType: AlertType;
  targetValue: number;
  triggered: boolean;
  triggeredAt: string | null;
  createdAt: string;
}

export type AlertType = 'PRICE_ABOVE' | 'PRICE_BELOW' | 'PERCENT_CHANGE_UP' | 'PERCENT_CHANGE_DOWN';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
