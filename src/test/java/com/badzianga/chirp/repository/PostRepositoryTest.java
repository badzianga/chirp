package com.badzianga.chirp.repository;

import com.badzianga.chirp.model.Post;
import com.badzianga.chirp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindPostsByUserId() {
        User user = userRepository.save(new User("test@email.com", "test", "password"));
        User anotherUser = userRepository.save(new User("another@email.com", "another", "password"));
        postRepository.save(new Post("Some content of the post", user));
        postRepository.save(new Post("Another post", user));
        postRepository.save(new Post("This should not be found", anotherUser));

        // when
        List<Post> posts = postRepository.findByAuthor_Id(user.getId());

        // then
        assertThat(posts).hasSize(2);
    }
}
