package compaintvar.convention.filter;

import compaintvar.convention.entity.Post;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotNull;

public class PostSpecifications {

    public static Specification<Post> equalsUserId(@NotNull Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
}

    public static Specification<Post> likeTitle(@NotNull String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Post> equalsTopicId(@NotNull Long topicId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("topicId"), topicId);
    }

    public static Specification<Post> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }
}
