package edworld.common.infra.repo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import edworld.common.infra.util.JSONUtil;
import edworld.common.repo.Criterio;
import edworld.common.repo.Repositorio;

public class RepositorioMemoria<T> implements Repositorio<T> {
	public List<T> listar(Principal principal, Criterio<T> criterio, Integer limite, String... orderBy) {
		return new ArrayList<>();
	}

	public void inserir(T item, Principal principal) {
		System.out.println("MUDANCA.INSERT:" + JSONUtil.toString(item));
	}

	public void alterar(T item, Principal principal) {
		System.out.println("MUDANCA.UPDATE:" + JSONUtil.toString(item));
	}

	public void excluir(T item, Principal principal) {
		System.out.println("MUDANCA.DELETE:" + JSONUtil.toString(item));
	}
}
