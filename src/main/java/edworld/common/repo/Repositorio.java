package edworld.common.repo;

import java.security.Principal;
import java.util.List;

public interface Repositorio<T> {
	List<T> listar(Principal principal, Criterio<T> criterio, Integer limite, String... orderBy);

	void inserir(T item, Principal principal);

	void alterar(T item, Principal principal) throws RepositorioException;

	void excluir(T item, Principal principal);
}
