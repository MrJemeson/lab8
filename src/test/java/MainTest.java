import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bmstu.Main;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @BeforeEach
    void setUp() {
        Main.FuelType.Vehicle.Station.reset();
        // Заполним топливо и цены
        Main.FuelType.Vehicle.Station.setPrice(Main.FuelType.PETROL_95, 1.0);
        Main.FuelType.Vehicle.Station.setPrice(Main.FuelType.DIESEL, 1.1);
        Main.FuelType.Vehicle.Station.setPrice(Main.FuelType.HYBRID, 0.5);

        Main.FuelType.Vehicle.Station.FuelTank.add(Main.FuelType.PETROL_95, 100.0);
        Main.FuelType.Vehicle.Station.FuelTank.add(Main.FuelType.DIESEL, 50.0);
        Main.FuelType.Vehicle.Station.FuelTank.add(Main.FuelType.HYBRID, 30.0);

        // Сброс уровней баков
        Main.FuelType.Vehicle.CAR1.reset(10);
        Main.FuelType.Vehicle.CAR2.reset(5);
        Main.FuelType.Vehicle.CAR3.reset(0);
    }

    @Test
    void testFuelByLitersNormal() {
        double cost = Main.FuelType.Vehicle.Station.Manager.Pump.P95.fuelByLiters(
                Main.FuelType.Vehicle.CAR1, 20);
        assertEquals(30.0, Main.FuelType.Vehicle.CAR1.getCurrentLevel(), 1e-9);
        assertEquals(20.0, Main.FuelType.Vehicle.Station.getSold(Main.FuelType.PETROL_95), 1e-9);
        assertEquals(20.0, cost, 1e-9);
    }

    @Test
    void testFuelToFull() {
        double cost = Main.FuelType.Vehicle.Station.Manager.Pump.P95.fuelToFull(
                Main.FuelType.Vehicle.CAR1);
        assertEquals(50.0, Main.FuelType.Vehicle.CAR1.getCurrentLevel(), 1e-9);
        assertEquals(40.0, Main.FuelType.Vehicle.Station.getSold(Main.FuelType.PETROL_95), 1e-9);
        assertEquals(40.0, cost, 1e-9);
    }

    @Test
    void testFuelByMoneyLimitedByStock() {
        double paid = Main.FuelType.Vehicle.Station.Manager.Pump.P95.fuelByMoney(
                Main.FuelType.Vehicle.CAR1, 150.0);
        // топлива всего 100 - currentLevel 10, бак 50, max 40 можно заправить
        assertEquals(50.0, Main.FuelType.Vehicle.CAR1.getCurrentLevel(), 1e-9);
        assertEquals(40.0, Main.FuelType.Vehicle.Station.getSold(Main.FuelType.PETROL_95), 1e-9);
        assertEquals(40.0, paid, 1e-9);
    }

    @Test
    void testWrongFuelThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                Main.FuelType.Vehicle.Station.Manager.Pump.P95.fuelByLiters(
                        Main.FuelType.Vehicle.CAR2, 10));
    }

    @Test
    void testHybridPump() {
        double paid = Main.FuelType.Vehicle.Station.Manager.Pump.HYBRID.fuelByLiters(
                Main.FuelType.Vehicle.CAR3, 10);
        assertEquals(10.0, Main.FuelType.Vehicle.CAR3.getCurrentLevel(), 1e-9);
        assertEquals(10.0 * 0.5, paid, 1e-9);
        assertThrows(IllegalArgumentException.class, () ->
                Main.FuelType.Vehicle.Station.Manager.Pump.HYBRID.fuelToFull(
                        Main.FuelType.Vehicle.CAR3));
    }

    @Test
    void testManagerRevenueAndOrder() {
        Main.FuelType.Vehicle.Station.Manager.Pump.P95.fuelByLiters(Main.FuelType.Vehicle.CAR1, 20);
        assertEquals(20.0, Main.FuelType.Vehicle.Station.Manager.revenue(), 1e-9);

        Main.FuelType.Vehicle.Station.Manager.order(Main.FuelType.PETROL_95, 50, 0.5);
        // закупка уменьшает выручку
        assertEquals(20.0 - 25.0, Main.FuelType.Vehicle.Station.Manager.revenue(), 1e-9);
    }

    @Test
    void testExhaustionThrows() {
        // заберем всё дизель
        Main.FuelType.Vehicle.Station.FuelTank.remove(Main.FuelType.DIESEL, 50.0);
        assertThrows(IllegalStateException.class, () ->
                Main.FuelType.Vehicle.Station.Manager.Pump.DIESEL.fuelByLiters(
                        Main.FuelType.Vehicle.CAR2, 10));
    }
}