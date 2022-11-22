package sf.lima.bookapp.user;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksByUserRepository extends CassandraRepository<BooksByUser,String > {

}
