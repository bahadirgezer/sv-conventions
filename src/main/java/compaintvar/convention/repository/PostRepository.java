package compaintvar.convention.repository;

import compaintvar.convention.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    boolean existsById(Long id);

    Post findPostByIdAndDeletedFalse(Long id);

}
