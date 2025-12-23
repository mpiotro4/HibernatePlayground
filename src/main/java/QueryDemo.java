import entity.Author;
import entity.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class QueryDemo {

    public static void main(String[] args) {
        SessionFactory sf = new Configuration()
                .configure()
                .buildSessionFactory();

        seedData(sf);
        //        jpql(sf);
        criteria(sf);
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

    private static void jpql(SessionFactory sf) {
        System.out.println("\n=== DEMO 1: JPQL ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            List<Author> authors = session.createQuery("SELECT a from Author a where SIZE(a.books)  > 1 ", Author.class).list();

            for (Author a : authors) {
                System.out.println(a);
            }

            session.getTransaction().commit();
        }
    }

    private static void hql() {

    }

    private static void criteria(SessionFactory sf) {
        System.out.println("\n=== DEMO 3: Criteria API ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();
            CriteriaBuilder cb = sf.getCriteriaBuilder();
            CriteriaQuery<Author> query = cb.createQuery(Author.class);
            Root<Author> author = query.from(Author.class);
            query.select(author).where(cb.gt(cb.size(author.get("books")), 1));
            List<Author> authors = session.createQuery(query).list();

            for (Author a : authors) {
                System.out.println(a);
            }
            session.getTransaction().commit();
        }
    }

    private static void criteriaWithPredicate(SessionFactory sf) {
        System.out.println("\n=== DEMO 4: Criteria API with Predicate ===");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            CriteriaBuilder cb = sf.getCriteriaBuilder();
            CriteriaQuery<Author> query = cb.createQuery(Author.class);
            Root<Author> author = query.from(Author.class);

            Predicate spec = authorWithMultipleBooks(cb, author);
            query.select(author).where(spec);

            List<Author> authors = session.createQuery(query).getResultList();

            for (Author a : authors) {
                System.out.println(a);
            }

            session.getTransaction().commit();
        }
    }

    private static Predicate authorWithMultipleBooks(CriteriaBuilder cb, Root<Author> author) {
        return cb.gt(cb.size(author.get("books")), 1);
    }
}
