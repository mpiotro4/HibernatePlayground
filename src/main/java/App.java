import entity.Author;
import entity.Book;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {

    public static void main(String[] args) {
        SessionFactory sf = new Configuration()
                .configure()
                .buildSessionFactory();

        seedData(sf);

        demoNPlusOne(sf);
        demoJoinFetch(sf);
        demoJoinFetchWithPagination(sf);
        demoPaginationFixed(sf);

        sf.close();
    }

    // =========================
    // DANE TESTOWE
    // =========================
    private static void seedData(SessionFactory sf) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();

            for (int i = 1; i <= 15; i++) {
                Author author = new Author("Author " + i);
                session.persist(author);

                for (int j = 1; j <= 10; j++) {
                    session.persist(new Book("Book " + i + "-" + j, author));
                }
            }

            session.getTransaction().commit();
        }
    }

    // =========================
    // DEMO 1: N+1
    // =========================
    private static void demoNPlusOne(SessionFactory sf) {
        System.out.println("\n=== DEMO 1: N+1 ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            var authors = session
                    .createQuery("from Author", Author.class)
                    .list();

            for (Author a : authors) {
                System.out.println(a.getName() + " -> " + a.getBooks().size());
            }

            session.getTransaction().commit();
        }
    }

    // =========================
    // DEMO 2: JOIN FETCH
    // =========================
    private static void demoJoinFetch(SessionFactory sf) {
        System.out.println("\n=== DEMO 2: JOIN FETCH ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            var authors = session.createQuery(
                    "select distinct a from Author a join fetch a.books",
                    Author.class
            ).list();

            for (Author a : authors) {
                System.out.println(a.getName() + " -> " + a.getBooks().size());
            }

            session.getTransaction().commit();
        }
    }

    // =========================
    // DEMO 3: JOIN FETCH + PAGINACJA (ŹLE)
    // =========================
    private static void demoJoinFetchWithPagination(SessionFactory sf) {
        System.out.println("\n=== DEMO 3: JOIN FETCH + pagination (WRONG) ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            var authors = session.createQuery(
                                         "select distinct a from Author a join fetch a.books order by a.id",
                                         Author.class
                                 )
                                 .setFirstResult(0)
                                 .setMaxResults(3)
                                 .list();

            System.out.println("Autorów: " + authors.size());
            for (Author a : authors) {
                System.out.println(a.getName() + " -> " + a.getBooks().size());
            }

            session.getTransaction().commit();
        }
    }

    // =========================
    // DEMO 4: PAGINACJA BEZ WARNINGU (POPRAWNIE)
    // =========================
    private static void demoPaginationFixed(SessionFactory sf) {
        System.out.println("\n=== DEMO 4: pagination FIXED ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            // 1. pobieramy ID autorów (paginacja działa poprawnie)
            List<Long> authorIds = session.createQuery(
                                                  "select a.id from Author a order by a.id",
                                                  Long.class
                                          )
                                          .setFirstResult(0)
                                          .setMaxResults(3)
                                          .list();

            // 2. dociągamy autorów z książkami
            var authors = session.createQuery(
                                         "select distinct a from Author a join fetch a.books where a.id in :ids",
                                         Author.class
                                 )
                                 .setParameter("ids", authorIds)
                                 .list();

            System.out.println("Autorów: " + authors.size());
            for (Author a : authors) {
                System.out.println(a.getName() + " -> " + a.getBooks().size());
            }

            session.getTransaction().commit();
        }
    }
}
