package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.entity.StreamSource;
import com.moxin.agvbackend.service.StreamSourceService;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreamControllerTest {

    @Mock
    private StreamSourceService streamSourceService;

    @InjectMocks
    private StreamController streamController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listSources_ShouldReturnSuccessResultWithStreamSources() {
        // Arrange
        List<StreamSource> mockSources = Arrays.asList(
                new StreamSource(1L, "Camera1", "http://cam1.mjpg", "mjpeg", "Front camera", LocalDateTime.now(), LocalDateTime.now()),
                new StreamSource(2L, "Camera2", "http://cam2.mjpg", "mjpeg", "Rear camera", LocalDateTime.now(), LocalDateTime.now())
        );
        when(streamSourceService.listAll()).thenReturn(mockSources);

        // Act
        Result result = streamController.listSources();

        // Assert
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());
        assertEquals(mockSources, result.getData());
        verify(streamSourceService, times(1)).listAll();
    }

    @Test
    void getDefaultStream_ShouldReturnSuccessResultWithFirstSourceUrl() {
        // Arrange
        List<StreamSource> mockSources = Arrays.asList(
                new StreamSource(1L, "Camera1", "http://cam1.mjpg", "mjpeg", "Front camera", LocalDateTime.now(), LocalDateTime.now()),
                new StreamSource(2L, "Camera2", "http://cam2.mjpg", "mjpeg", "Rear camera", LocalDateTime.now(), LocalDateTime.now())
        );
        when(streamSourceService.listAll()).thenReturn(mockSources);

        // Act
        Result result = streamController.getDefaultStream();

        // Assert
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());
        assertEquals("http://cam1.mjpg", result.getData());
        verify(streamSourceService, times(1)).listAll();
    }

    @Test
    void getDefaultStream_ShouldThrowAppExceptionWhenNoSourcesAvailable() {
        // Arrange
        when(streamSourceService.listAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            streamController.getDefaultStream();
        });

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("暂无可用视频源", exception.getMessage());
        verify(streamSourceService, times(1)).listAll();
    }

    @Test
    void getDefaultStream_ShouldReturnFirstSourceEvenWithSingleSource() {
        // Arrange
        List<StreamSource> mockSources = Collections.singletonList(
                new StreamSource(1L, "Camera1", "http://cam1.mjpg", "mjpeg", "Front camera", LocalDateTime.now(), LocalDateTime.now())
        );
        when(streamSourceService.listAll()).thenReturn(mockSources);

        // Act
        Result result = streamController.getDefaultStream();

        // Assert
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());
        assertEquals("http://cam1.mjpg", result.getData());
        verify(streamSourceService, times(1)).listAll();
    }
}