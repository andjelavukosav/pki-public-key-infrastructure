export interface SessionInfo {
  sessionId: string;
  ipAddress: string;
  userAgent: string;
  device: string;
  lastActivity: Date;
  userId?: number;
}
