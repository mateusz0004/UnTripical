import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import type { LoginRequest, UserRegisterRequestDTO, UserResponseDTO } from '../api/types';
import { getMe, login as loginApi, registerUser } from '../api/client';
import { clearToken, getToken, setToken } from './tokenStorage';

export type AuthStatus = 'anonymous' | 'loading' | 'authenticated';

type AuthContextValue = {
  status: AuthStatus;
  token: string | null;
  user: UserResponseDTO | null;
  login: (dto: LoginRequest) => Promise<void>;
  register: (dto: UserRegisterRequestDTO) => Promise<void>;
  logout: () => void;
  refreshMe: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(() => getToken());
  const [user, setUser] = useState<UserResponseDTO | null>(null);
  const [status, setStatus] = useState<AuthStatus>(() => (getToken() ? 'loading' : 'anonymous'));

  const refreshMe = useCallback(async () => {
    const t = getToken();
    if (!t) {
      setTokenState(null);
      setUser(null);
      setStatus('anonymous');
      return;
    }

    setStatus('loading');
    try {
      const me = await getMe();
      setTokenState(t);
      setUser(me);
      setStatus('authenticated');
    } catch {
      clearToken();
      setTokenState(null);
      setUser(null);
      setStatus('anonymous');
    }
  }, []);

  useEffect(() => {
    void refreshMe();
  }, [refreshMe]);

  const login = useCallback(async (dto: LoginRequest) => {
    setStatus('loading');
    const t = await loginApi(dto);
    setToken(t);
    setTokenState(t);
    await refreshMe();
  }, [refreshMe]);

  const register = useCallback(async (dto: UserRegisterRequestDTO) => {
    await registerUser(dto);
    await login({ username: dto.username, password: dto.password });
  }, [login]);

  const logout = useCallback(() => {
    clearToken();
    setTokenState(null);
    setUser(null);
    setStatus('anonymous');
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({ status, token, user, login, register, logout, refreshMe }),
    [status, token, user, login, register, logout, refreshMe]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
