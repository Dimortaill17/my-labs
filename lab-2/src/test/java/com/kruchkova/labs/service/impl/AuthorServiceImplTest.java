package com.kruchkova.labs.service.impl;

import com.kruchkova.labs.entity.AuthorEntity;
import com.kruchkova.labs.exception.ServiceException;
import com.kruchkova.labs.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для AuthorServiceImpl")
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private AuthorEntity testAuthor;
    private static final int TEST_ID = 1;
    private static final String TEST_NAME = "Лев Толстой";
    private static final int TEST_BIRTH_YEAR = 1828;

    @BeforeEach
    void setUp() {
        testAuthor = new AuthorEntity(TEST_ID, TEST_NAME, TEST_BIRTH_YEAR);
    }

    // ТЕСТЫ ДЛЯ МЕТОДА SAVE

    @Test
    @DisplayName("save: успешное создание автора с валидными данными")
    void save_withValidData_returnsGeneratedId() {
        when(authorRepository.save(any(AuthorEntity.class))).thenReturn(TEST_ID);

        int resultId = authorService.save(TEST_NAME, TEST_BIRTH_YEAR);

        assertEquals(TEST_ID, resultId);
        verify(authorRepository, times(1)).save(any(AuthorEntity.class));
    }

    @Test
    @DisplayName("save: бросает исключение при пустом имени")
    void save_withEmptyName_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.save("", TEST_BIRTH_YEAR)
        );
        assertTrue(exception.getMessage().contains("пустым"));
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: бросает исключение при годе из будущего")
    void save_withFutureBirthYear_throwsServiceException() {
        int futureYear = Year.now().getValue() + 10;
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.save(TEST_NAME, futureYear)
        );
        assertTrue(exception.getMessage().contains("диапазоне"));
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: бросает исключение при слишком древнем годе")
    void save_withAncientBirthYear_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.save(TEST_NAME, 500)
        );
        assertTrue(exception.getMessage().contains("диапазоне"));
        verify(authorRepository, never()).save(any());
    }

    // ТЕСТЫ ДЛЯ МЕТОДА FIND BY ID

    @Test
    @DisplayName("findById: успешный поиск существующего автора")
    void findById_withExistingId_returnsAuthor() {
        when(authorRepository.findById(TEST_ID)).thenReturn(Optional.of(testAuthor));

        AuthorEntity result = authorService.findById(TEST_ID);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_NAME, result.getName());
        verify(authorRepository).findById(TEST_ID);
    }

    @Test
    @DisplayName("findById: бросает исключение при отсутствии автора")
    void findById_withNonExistingId_throwsServiceException() {
        when(authorRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.findById(TEST_ID)
        );
        assertTrue(exception.getMessage().contains("не найден"));
        verify(authorRepository).findById(TEST_ID);
    }

    // ТЕСТЫ ДЛЯ МЕТОДА FIND BY NAME

    @Test
    @DisplayName("findByName: успешный поиск по существующему имени")
    void findByName_withExistingName_returnsAuthor() {
        // В репозитории метод findByField возвращает объект или null
        when(authorRepository.findByField(TEST_NAME)).thenReturn(testAuthor);

        AuthorEntity result = authorService.findByName(TEST_NAME);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(authorRepository).findByField(TEST_NAME);
    }

    @Test
    @DisplayName("findByName: бросает исключение при отсутствии автора с таким именем")
    void findByName_withNonExistingName_throwsServiceException() {
        when(authorRepository.findByField("Неизвестный")).thenReturn(null);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.findByName("Неизвестный")
        );
        assertTrue(exception.getMessage().contains("не найден"));
        verify(authorRepository).findByField("Неизвестный");
    }

    @Test
    @DisplayName("findByName: бросает исключение при пустом имени для поиска")
    void findByName_withEmptyName_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.findByName("")
        );
        assertTrue(exception.getMessage().contains("пустым"));
        verify(authorRepository, never()).findByField(any());
    }

    // ТЕСТЫ ДЛЯ МЕТОДА FIND ALL

    @Test
    @DisplayName("findAll: возвращает пустой список если авторов нет")
    void findAll_whenNoAuthors_returnsEmptyList() {
        when(authorRepository.findAll()).thenReturn(List.of());

        List<AuthorEntity> result = authorService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(authorRepository).findAll();
    }

    @Test
    @DisplayName("findAll: возвращает список всех авторов")
    void findAll_withAuthors_returnsAllAuthors() {
        List<AuthorEntity> expectedAuthors = Arrays.asList(
                testAuthor,
                new AuthorEntity(2, "Достоевский", 1821)
        );
        when(authorRepository.findAll()).thenReturn(expectedAuthors);

        List<AuthorEntity> result = authorService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(authorRepository).findAll();
    }

    // ТЕСТЫ ДЛЯ МЕТОДА UPDATE

    @Test
    @DisplayName("update: успешное обновление существующего автора")
    void update_withValidAuthor_updatesSuccessfully() {
        AuthorEntity updatedAuthor = new AuthorEntity(TEST_ID, "Обновлённое имя", 1830);
        when(authorRepository.findById(TEST_ID)).thenReturn(Optional.of(testAuthor));
        when(authorRepository.update(updatedAuthor)).thenReturn(true);

        authorService.update(updatedAuthor);

        verify(authorRepository).findById(TEST_ID);
        verify(authorRepository).update(updatedAuthor);
    }

    @Test
    @DisplayName("update: бросает исключение при обновлении несуществующего автора")
    void update_withNonExistingAuthor_throwsServiceException() {
        when(authorRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.update(testAuthor)
        );
        assertTrue(exception.getMessage().contains("не найден"));
        verify(authorRepository).findById(TEST_ID);
        verify(authorRepository, never()).update(any());
    }

    @Test
    @DisplayName("update: бросает исключение при null-объекте")
    void update_withNullAuthor_throwsServiceException() {
        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.update(null)
        );
        assertTrue(exception.getMessage().contains("null"));
        verify(authorRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("update: бросает исключение если репозиторий вернул false")
    void update_whenRepositoryReturnsFalse_throwsServiceException() {
        when(authorRepository.findById(TEST_ID)).thenReturn(Optional.of(testAuthor));
        when(authorRepository.update(any(AuthorEntity.class))).thenReturn(false);

        ServiceException exception = assertThrows(
                ServiceException.class,
                () -> authorService.update(testAuthor)
        );
        assertTrue(exception.getMessage().contains("Ошибка при обновлении"));
        verify(authorRepository).update(any(AuthorEntity.class));
    }

    // ТЕСТЫ ДЛЯ МЕТОДА DELETE BY ID

    @Test
    @DisplayName("deleteById: успешное удаление существующего автора")
    void deleteById_withExistingId_deletesAuthor() {
        authorService.deleteById(TEST_ID);
        verify(authorRepository).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("deleteById: удаление несуществующего автора не бросает исключение")
    void deleteById_withNonExistingId_doesNotThrow() {
        assertDoesNotThrow(() -> authorService.deleteById(999));
        verify(authorRepository).deleteById(999);
    }
}