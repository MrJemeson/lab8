import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bmstu.Main;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @BeforeEach
    void setUp() {
        Main.FuelType.Vehicle.Station.reset();
        Main.FuelType.Vehicle.Station.setPrice(Main.FuelType.PETROL, 1.0);
        Main.FuelType.Vehicle.Station.setPrice(Main.FuelType.DIESEL, 1.1);
        Main.FuelType.Vehicle.Station.setPrice(Main.FuelType.HYBRID, 0.5);

        Main.FuelType.Vehicle.Station.FuelTank.add(Main.FuelType.PETROL, 100.0);
        Main.FuelType.Vehicle.Station.FuelTank.add(Main.FuelType.DIESEL, 50.0);
        Main.FuelType.Vehicle.Station.FuelTank.add(Main.FuelType.HYBRID, 30.0);

        Main.FuelType.Vehicle.CAR1.r(10);
    }

    @Test
    void testFuelByLitersNormal() {
        double cost = Main.FuelType.Vehicle.Station.Manager.Pump.P.fBL(
                Main.FuelType.Vehicle.CAR1, 20);
        assertEquals(30.0, Main.FuelType.Vehicle.CAR1.getcL(), 1e-9);
        assertEquals(20.0, Main.FuelType.Vehicle.Station.getSold(Main.FuelType.PETROL), 1e-9);
        assertEquals(20.0, cost, 1e-9);
    }

    @Test
    void testFuelToFull() {
        double cost = Main.FuelType.Vehicle.Station.Manager.Pump.P.fTF(
                Main.FuelType.Vehicle.CAR1);
        assertEquals(50.0, Main.FuelType.Vehicle.CAR1.getcL(), 1e-9);
        assertEquals(40.0, Main.FuelType.Vehicle.Station.getSold(Main.FuelType.PETROL), 1e-9);
        assertEquals(40.0, cost, 1e-9);
    }

    @Test
    void testFuelByMoneyLimitedByStock() {
        double paid = Main.FuelType.Vehicle.Station.Manager.Pump.P.fBM(
                Main.FuelType.Vehicle.CAR1, 150.0);
        // топлива всего 100 - currentLevel 10, бак 50, max 40 можно заправить
        assertEquals(50.0, Main.FuelType.Vehicle.CAR1.getcL(), 1e-9);
        assertEquals(40.0, Main.FuelType.Vehicle.Station.getSold(Main.FuelType.PETROL), 1e-9);
        assertEquals(40.0, paid, 1e-9);
    }

    @Test
    void testManagerRevenueAndOrder() {
        Main.FuelType.Vehicle.Station.Manager.Pump.P.fBL(Main.FuelType.Vehicle.CAR1, 20);
        assertEquals(20.0, Main.FuelType.Vehicle.Station.Manager.r(), 1e-9);

        Main.FuelType.Vehicle.Station.Manager.o(Main.FuelType.PETROL, 50, 0.5);
        assertEquals(20.0 - 25.0, Main.FuelType.Vehicle.Station.Manager.r(), 1e-9);
    }
}