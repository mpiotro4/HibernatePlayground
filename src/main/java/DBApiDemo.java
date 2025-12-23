import entity.Author;
import entity.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DBApiDemo {

    public static void main(String[] args) {
        sessionDemo();
        entityManagerDemo();
    }

    public static void sessionDemo() {
        // Utworzenie SessionFactory (zazwyczaj raz na aplikację)
        SessionFactory sf = new Configuration()
                .configure()
                .buildSessionFactory();

        Session session = sf.openSession();

        try {
            // Rozpoczęcie transakcji
            session.beginTransaction();

            // Tworzenie nowego autora i książki
            Author author = new Author("Jan Kowalski");
            session.persist(author);

            Book book1 = new Book("Hibernate w praktyce", author);
            Book book2 = new Book("JPA dla początkujących", author);

            // Zapisanie do bazy
            session.persist(book1);
            session.persist(book2);

            System.out.println("✓ Zapisano autora: " + author.getName());

            // Zatwierdzenie i zamkniecie transakcji
            session.getTransaction().commit();
            session.close();

            // Nowa transakcja do odczytu
            session = sf.openSession();
            session.beginTransaction();

            // Pobranie autora z bazy
            Author found = session.get(Author.class, author.getId());
            System.out.println("\n✓ Pobrano autora: " + found.getName());
            System.out.println("✓ Liczba książek: " + found.getBooks().size());

            // Wyświetlenie książek
            System.out.println("\nKsiążki autora:");
            found.getBooks().forEach(book ->
                    System.out.println("  - " + book.getTitle())
            );

            session.getTransaction().commit();

        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            sf.close();
        }
    }

    public static void entityManagerDemo() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
        EntityManager em = emf.createEntityManager();
        em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Author author = new Author("Jan Kowalski");
            em.persist(author);
            Book book1 = new Book("Hibernate w praktyce", author);
            Book book2 = new Book("EntityManager dla początkujących", author);
            em.persist(book1);
            em.persist(book2);

            System.out.println("✓ Zapisano autora: " + author.getName());
            em.getTransaction().commit();
            em.close();

            em = emf.createEntityManager();
            em.getTransaction().begin();

            Author found = em.find(Author.class, author.getId());
            System.out.println("\n✓ Pobrano autora: " + found.getName());
            System.out.println("✓ Liczba książek: " + found.getBooks().size());

            System.out.println("\nKsiążki autora:");
            found.getBooks().forEach(book ->
                    System.out.println("  - " + book.getTitle())
            );

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}