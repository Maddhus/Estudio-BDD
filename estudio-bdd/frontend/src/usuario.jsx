import { createContext, useContext } from "react";

export const UsuarioContext = createContext(null);

/** Usuário de demonstração atual e a função para trocá-lo. */
export function useUsuario() {
  return useContext(UsuarioContext);
}
