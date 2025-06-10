import api from "src/api/tallerApi";
import type { LoginRequest, LoginResponse } from "src/types/auth";

class AuthService {
  async login(credentials: LoginRequest): Promise<LoginResponse | undefined> {
    try {
      const response = await api.post("/auth/signin", credentials);
      return response.data as LoginResponse;
    } catch (error) {
      console.error(error);
      return undefined;
    }
  }

  async logout(): Promise<void> {
    try {
      await api.post("/auth/signout");
    } catch (error) {
      console.error(error);
    }
  }
}

export const authService = new AuthService();
