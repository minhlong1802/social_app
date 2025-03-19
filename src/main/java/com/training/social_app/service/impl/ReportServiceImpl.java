package com.training.social_app.service.impl;

import com.training.social_app.entity.User;
import com.training.social_app.repository.*;
import com.training.social_app.service.ReportService;
import com.training.social_app.utils.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final PostRepository postRepository;
    private final FriendShipRepository friendShipRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private Integer getCurrentUserId() {
        User currentUser = userRepository.findById(UserContext.getUser().getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));
        return currentUser.getId();
    }

    @Override
    public byte[] generateWeeklyReport() {
        Integer userId = getCurrentUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));
        return generateExcelFile(currentUser);
    }

    private byte[] generateExcelFile(User user) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getWeeksInWeekYear());
            if(cal.getFirstDayOfWeek() != Calendar.MONDAY){
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            }
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            LocalDate startDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = LocalDateTime.now();
            // Create Excel data
            List<List<String>> excelData = Arrays.asList(
                    Arrays.asList("Username", "Posts Last Week", "New Friends", "New Likes", "New Comments"),
                    Arrays.asList(
                            user.getUsername(),
                            String.valueOf(postRepository.countByUserIdAndCreatedAtBetween(user.getId(), startDateTime,endDateTime)),
                            String.valueOf(friendShipRepository.countNewFriendsByUserIdInPastWeek(user.getId(),startDateTime,endDateTime)),
                            String.valueOf(likeRepository.countLikesByUserAndDate(user.getId(), startDateTime,endDateTime)),
                            String.valueOf(commentRepository.countCommentsByUserIdAndCreatedAtBetween(user.getId(), startDateTime,endDateTime))
                    )
            );

            // Write to Excel
            Sheet sheet = workbook.createSheet("Weekly Report");

            // Apply header styling
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Apply data cell styling
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);

            // Write headers with style
            Row headerRow = sheet.createRow(0);
            List<String> headers = excelData.get(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Write data rows
            Row dataRow = sheet.createRow(1);
            List<String> rowData = excelData.get(1);
            for (int j = 0; j < rowData.size(); j++) {
                Cell cell = dataRow.createCell(j);
                cell.setCellValue(rowData.get(j));
                cell.setCellStyle(dataCellStyle);
            }

            // Auto-size columns for better readability
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }
}