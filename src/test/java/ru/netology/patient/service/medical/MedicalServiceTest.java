package ru.netology.patient.service.medical;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MedicalServiceTest {

    private static PatientInfo patientInfo;
    private static final BigDecimal normalTemperature = new BigDecimal(
            "36.444444444444444444444444444444444444444444444444444444453345");
    private static final BloodPressure bloodPressure = new BloodPressure(160, 90);
    private static final String USER_ID = "user1";

    @BeforeAll
    static void init() {
        patientInfo = new PatientInfo(USER_ID, "Ivan", "Ivanov", LocalDate.now().minusYears(25),
                new HealthInfo(normalTemperature, bloodPressure));
    }

    @Test
    void checkBloodPressureWarningTest() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(USER_ID)).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkBloodPressure(USER_ID, new BloodPressure(150, 80));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        assertEquals(getWarning(), argumentCaptor.getValue());
    }

    @Test
    void checkTemperatureWarningTest() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(USER_ID)).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkTemperature(USER_ID, new BigDecimal("34.0"));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        assertEquals(getWarning(), argumentCaptor.getValue());
    }

    @Test
    void checkBloodPressureNormalTest() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(USER_ID)).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkBloodPressure(USER_ID, bloodPressure);

        Mockito.verify(alertService, Mockito.never()).send(getWarning());
    }

    @Test
    void checkTemperatureNormalTest() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(USER_ID)).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkTemperature(USER_ID, normalTemperature);

        Mockito.verify(alertService, Mockito.never()).send(getWarning());
    }

    private String getWarning() {
        return format("Warning, patient with id: %s, need help", USER_ID);
    }
}
