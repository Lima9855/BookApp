package sf.lima.bookapp.user;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksByUserRepository extends CassandraRepository<BooksByUser,String > {

    Slice<BooksByUser> findAllById(String id, Pageable pageable);
}
