package org.dnu.novomlynov.library.repository;

import org.dnu.novomlynov.library.TestcontainersConfiguration;
import org.dnu.novomlynov.library.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@Transactional
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void clear() {
        categoryRepository.deleteAll();
    }

    @Test
    void shouldSaveCategory() {
        // given
        Category category = Category.builder()
                .name("Fiction")
                .build();

        // when
        Category savedCategory = categoryRepository.save(category);

        // then
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Fiction");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        // given
        categoryRepository.saveAll(List.of(
                Category.builder().name("Science Fiction").build(),
                Category.builder().name("Fantasy").build(),
                Category.builder().name("Biography").build()));

        // when
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase("fiction");

        // then
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getName()).isEqualTo("Science Fiction");
    }

    @Test
    void shouldFindMultipleCategoriesWithSamePartialName() {
        // given
        categoryRepository.saveAll(List.of(
                Category.builder().name("Science Fiction").build(),
                Category.builder().name("Historical Fiction").build(),
                Category.builder().name("Fantasy").build()));

        // when
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase("fiction");

        // then
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Science Fiction", "Historical Fiction");
    }

    @Test
    void shouldFindByNameIgnoreCase() {
        // given
        categoryRepository.saveAll(List.of(
                Category.builder().name("Poetry").build(),
                Category.builder().name("Drama").build(),
                Category.builder().name("Mystery").build()));

        // when
        Optional<Category> category = categoryRepository.findByNameIgnoreCase("drama");

        // then
        assertThat(category).isPresent();
        assertThat(category.get().getName()).isEqualTo("Drama");
    }

    @Test
    void shouldReturnEmptyWhenCategoryNotFoundByName() {
        // given
        categoryRepository.saveAll(List.of(
                Category.builder().name("Poetry").build(),
                Category.builder().name("Drama").build()));

        // when
        Optional<Category> category = categoryRepository.findByNameIgnoreCase("nonexistent");

        // then
        assertThat(category).isEmpty();
    }

    @Test
    void shouldCheckIfCategoryExistsByNameIgnoreCase() {
        // given
        Category category = Category.builder()
                .name("Self-Help")
                .build();
        categoryRepository.save(category);

        // when & then
        assertThat(categoryRepository.existsByNameIgnoreCase("self-help")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("SELF-HELP")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("Self-Help")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("Horror")).isFalse();
    }

    @Test
    void shouldFindAllCategories() {
        // given
        categoryRepository.saveAll(List.of(
                Category.builder().name("Category 1").build(),
                Category.builder().name("Category 2").build(),
                Category.builder().name("Category 3").build()));

        // when
        List<Category> categories = categoryRepository.findAll();

        // then
        assertThat(categories).hasSize(3);
        assertThat(categories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Category 1", "Category 2", "Category 3");
    }
}
