package ru.bmstu;

public enum Main {
    ;

    public enum FuelType {
        PETROL_80, PETROL_95, PETROL_98, DIESEL, HYBRID, ELECTRIC;
        public enum Vehicle {
            CAR1(FuelType.PETROL_95, 50, 10);

            public enum Station {
                ;
                public enum Manager {
                    ;
                    public enum Pump {
                        P95(Kind.NORMAL, FuelType.PETROL_95),
                        DIESEL(Kind.NORMAL, FuelType.DIESEL),
                        HYBRID(Kind.HYBRID_ONLY, FuelType.HYBRID),
                        ELECTRIC(Kind.ELECTRIC_ONLY, FuelType.ELECTRIC);

                        public enum Kind { NORMAL, HYBRID_ONLY, ELECTRIC_ONLY }

                        private final Kind kind;
                        private final FuelType supported;

                        Pump(Kind kind, FuelType supported) {
                            this.kind = kind;
                            this.supported = supported;
                        }

                        private void check(FuelType.Vehicle v) {
                            if (v.getFuelType() != supported)
                                throw new IllegalArgumentException("Неподдерживаемый тип топлива");
                        }

                        public double fuelByLiters(FuelType.Vehicle v, double liters) {
                            check(v);
                            double canFill = Math.min(liters, v.getNeededToFull());
                            double taken = FuelTank.remove(v.getFuelType(), canFill);
                            if (taken <= 0) throw new IllegalStateException("Нет топлива");
                            v.addFuel(taken);
                            double cost = taken * FuelType.Vehicle.Station.getPrice(v.getFuelType());
                            FuelType.Vehicle.Station.recordSale(v.getFuelType(), taken, cost);
                            return cost;
                        }

                        public double fuelToFull(FuelType.Vehicle v) {
                            if (kind != Kind.NORMAL) throw new IllegalArgumentException("Нельзя");
                            return fuelByLiters(v, v.getNeededToFull());
                        }

                        public double fuelByMoney(FuelType.Vehicle v, double money) {
                            if (kind != Kind.NORMAL) throw new IllegalArgumentException("Нельзя");
                            double liters = money / FuelType.Vehicle.Station.getPrice(v.getFuelType());
                            return fuelByLiters(v, liters);
                        }
                    }

                    public static double revenue() { return FuelType.Vehicle.Station.getRevenue(); }
                    public static double sold(FuelType t) { return FuelType.Vehicle.Station.getSold(t); }
                    public static void order(FuelType t, double amt, double cost) { FuelType.Vehicle.Station.orderTanker(t, amt, cost); }
                }

                private static final java.util.EnumMap<FuelType, Double> prices =
                        new java.util.EnumMap<>(FuelType.class);
                private static final java.util.EnumMap<FuelType, Double> sold =
                        new java.util.EnumMap<>(FuelType.class);
                private static double revenue = 0.0;

                static {
                    for (FuelType t : FuelType.values()) {
                        prices.put(t, 0.0);
                        sold.put(t, 0.0);
                    }
                }

                public static void setPrice(FuelType t, double price) { prices.put(t, price); }
                public static double getPrice(FuelType t) { return prices.get(t); }

                public static void recordSale(FuelType t, double liters, double money) {
                    sold.put(t, sold.get(t) + liters);
                    revenue += money;
                }

                public static double getRevenue() { return revenue; }
                public static double getSold(FuelType t) { return sold.get(t); }

                public static void orderTanker(FuelType t, double amount, double costPerLiter) {
                    FuelTank.add(t, amount);
                    revenue -= amount * costPerLiter;
                }

                public enum FuelTank {
                    ;
                    private static final java.util.EnumMap<FuelType, Double> volumes =
                            new java.util.EnumMap<>(FuelType.class);

                    static {
                        for (FuelType t : FuelType.values()) volumes.put(t, 0.0);
                    }

                    public static double getVolume(FuelType type) { return volumes.get(type); }

                    public static void add(FuelType type, double amount) {
                        if (amount < 0) throw new IllegalArgumentException();
                        volumes.put(type, getVolume(type) + amount);
                    }

                    public static double remove(FuelType type, double amount) {
                        double avail = getVolume(type);
                        double taken = Math.min(avail, amount);
                        volumes.put(type, avail - taken);
                        return taken;
                    }

                    public static void reset() {
                        for (FuelType t : FuelType.values()) volumes.put(t, 0.0);
                    }
                }

                public static void reset() {
                    for (FuelType t : FuelType.values()) {
                        prices.put(t, 0.0);
                        sold.put(t, 0.0);
                    }
                    revenue = 0.0;
                    FuelTank.reset();
                }
            }

            private final FuelType fuelType;
            private final double tankCapacity;
            private double currentLevel;

            Vehicle(FuelType fuelType, double tankCapacity, double currentLevel) {
                this.fuelType = fuelType;
                this.tankCapacity = tankCapacity;
                this.currentLevel = currentLevel;
            }

            public FuelType getFuelType() { return fuelType; }
            public double getTankCapacity() { return tankCapacity; }
            public double getCurrentLevel() { return currentLevel; }
            public double getNeededToFull() { return tankCapacity - currentLevel; }

            public void addFuel(double amount) {
                if (amount < 0) throw new IllegalArgumentException("Отрицательное количество");
                if (currentLevel + amount > tankCapacity + 1e-9)
                    throw new IllegalArgumentException("Переполнение бака");
                currentLevel += amount;
            }

            public void reset(double level) { this.currentLevel = level; }
        }
    }


    public static void main(String[] args) {
        FuelType.Vehicle.Station.reset();
        FuelType.Vehicle.Station.setPrice(FuelType.PETROL_95, 1.2);
        FuelType.Vehicle.Station.FuelTank.add(FuelType.PETROL_95, 200);

        FuelType.Vehicle.CAR1.reset(10);
        double cost = FuelType.Vehicle.Station.Manager.Pump.P95.fuelToFull(FuelType.Vehicle.CAR1);

        System.out.println("Заправка: " + cost + ", теперь в баке: " + FuelType.Vehicle.CAR1.getCurrentLevel());
        System.out.println("Выручка: " + FuelType.Vehicle.Station.Manager.revenue());
    }
}
