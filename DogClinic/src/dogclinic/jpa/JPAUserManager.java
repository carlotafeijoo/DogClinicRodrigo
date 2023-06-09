package dogclinic.jpa;

import java.util.List;

import javax.persistence.*;

import dogclinic.ifaces.UserManager;
import dogclinic.pojos.*;

public class JPAUserManager implements UserManager {

	EntityManager em;

	public JPAUserManager() {
		em = Persistence.createEntityManagerFactory("dogclinic-provider").createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("PRAGMA foreign_keys=ON").executeUpdate();
		em.getTransaction().commit();
		// Create the needed roles
		if (this.getRoles().isEmpty()) {
			Role owner = new Role("owner");
			Role vet = new Role("vet");
			this.createRole(vet);
			this.createRole(owner);
		}
	}

	public void close() {
		em.close();
	}

	@Override
	public void register(User user) {
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
	}

	@Override
	public void createRole(Role role) {
		em.getTransaction().begin();
		em.persist(role);
		em.getTransaction().commit();
	}

	@Override
	public void assignRole(User user, Role role) {
		em.getTransaction().begin();
		user.setRole(role);
		role.addUser(user);
		em.getTransaction().commit();
	}

	@Override
	public User login(String name, String password) {
		try {
			Query q = em.createNativeQuery("SELECT * FROM users WHERE username = ? AND password = ?", User.class);
			q.setParameter(1, name);
			q.setParameter(2, password);
			User user = (User) q.getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Role> getRoles() {
		Query q = em.createNativeQuery("SELECT * FROM roles", Role.class);
		List<Role> roles = (List<Role>) q.getResultList();
		return roles;
	}

	@Override
	public Role getRole(String name) {
		Query q = em.createNativeQuery("SELECT * FROM roles WHERE name LIKE ?", Role.class);
		q.setParameter(1, name);
		Role r = (Role) q.getSingleResult();
		return r;
	}

}
