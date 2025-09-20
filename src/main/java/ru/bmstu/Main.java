package ru.bmstu;

import java.util.Scanner;

public enum Main {
    ;
    public enum FuelType {
        PETROL, DIESEL, HYBRID, ELECTRIC;
        public enum Vehicle {
            CAR1(FuelType.PETROL, 50, 10);

            public enum Station {
                ;
                public enum Manager {
                    ;
                    public enum Pump {
                        P(Kind.NORMAL, FuelType.PETROL),
                        DIESEL(Kind.NORMAL, FuelType.DIESEL),
                        HYBRID(Kind.HYBRID, FuelType.HYBRID),
                        ELECTRIC(Kind.ELECTRIC, FuelType.ELECTRIC);

                        public enum Kind { NORMAL, HYBRID, ELECTRIC}

                        private final Kind k;
                        private final FuelType s;

                        Pump(Kind k, FuelType s) {
                            this.k = k;
                            this.s = s;
                        }

                        private void c(FuelType.Vehicle v) {
                            if (v.getfT() != s)
                                throw new IllegalArgumentException("Неподдерживаемый тип топлива");
                        }

                        public double fBL(FuelType.Vehicle v, double l) {
                            c(v);
                            double cF = Math.min(l, v.getNTF());
                            double t = FuelTank.remove(v.getfT(), cF);
                            if (t <= 0) throw new IllegalStateException("Нет топлива");
                            v.addF(t);
                            double c = t * FuelType.Vehicle.Station.getPrice(v.getfT());
                            FuelType.Vehicle.Station.recordSale(v.getfT(), t, c);
                            return c;
                        }

                        public double fTF(FuelType.Vehicle v) {
                            if (k != Kind.NORMAL) throw new IllegalArgumentException("Нельзя");
                            return fBL(v, v.getNTF());
                        }

                        public double fBM(FuelType.Vehicle v, double m) {
                            if (k != Kind.NORMAL) throw new IllegalArgumentException("Нельзя");
                            double l = m / FuelType.Vehicle.Station.getPrice(v.getfT());
                            return fBL(v, l);
                        }
                    }

                    public static double r() { return FuelType.Vehicle.Station.getR(); }
                    public static double s(FuelType t) { return FuelType.Vehicle.Station.getSold(t); }
                    public static void o(FuelType t, double amt, double cost) { FuelType.Vehicle.Station.orderTanker(t, amt, cost); }
                }

                private static final java.util.EnumMap<FuelType, Double> p =
                        new java.util.EnumMap<>(FuelType.class);
                private static final java.util.EnumMap<FuelType, Double> s =
                        new java.util.EnumMap<>(FuelType.class);
                private static double r = 0.0;

                static {
                    for (FuelType t : FuelType.values()) {
                        p.put(t, 0.0);
                        s.put(t, 0.0);
                    }
                }

                public static void setPrice(FuelType t, double price) { p.put(t, price); }
                public static double getPrice(FuelType t) { return p.get(t); }

                public static void recordSale(FuelType t, double l, double m) {
                    s.put(t, s.get(t) + l);
                    r += m;
                }

                public static double getR() { return r; }
                public static double getSold(FuelType t) { return s.get(t); }

                public static void orderTanker(FuelType t, double a, double cPL) {
                    FuelTank.add(t, a);
                    r -= a * cPL;
                }

                public enum FuelTank {
                    ;
                    private static final java.util.EnumMap<FuelType, Double> v =
                            new java.util.EnumMap<>(FuelType.class);

                    static {
                        for (FuelType t : FuelType.values()) v.put(t, 0.0);
                    }

                    public static double getV(FuelType t) { return v.get(t); }

                    public static void add(FuelType t, double a) {
                        if (a < 0) throw new IllegalArgumentException();
                        v.put(t, getV(t) + a);
                    }

                    public static double remove(FuelType t, double a) {
                        double av = getV(t);
                        double ta = Math.min(av, a);
                        v.put(t, av - ta);
                        return ta;
                    }

                    public static void reset() {
                        for (FuelType t : FuelType.values()) v.put(t, 0.0);
                    }
                }

                public static void reset() {
                    for (FuelType t : FuelType.values()) {
                        p.put(t, 0.0);
                        s.put(t, 0.0);
                    }
                    r = 0.0;
                    FuelTank.reset();
                }
            }

            private final FuelType fT;
            private final double tC;
            private double cL;

            Vehicle(FuelType fT, double tC, double cL) {
                this.fT = fT;
                this.tC = tC;
                this.cL = cL;
            }

            public FuelType getfT() { return fT; }
            public double gettC() { return tC; }
            public double getcL() { return cL; }
            public double getNTF() { return tC - cL; }

            public void addF(double amount) {
                if (amount < 0) throw new IllegalArgumentException("Отрицательное количество");
                if (cL + amount > tC + 1e-9)
                    throw new IllegalArgumentException("Переполнение бака");
                cL += amount;
            }

            public void r(double l) { this.cL = l; }
        }
    }


    public static void main(String[] args) {
        FuelType.Vehicle.Station.reset();
        FuelType.Vehicle.Station.setPrice(FuelType.PETROL, 1.2);
        FuelType.Vehicle.Station.FuelTank.add(FuelType.PETROL, 200);
        Scanner scanner = new Scanner(System.in);
        FuelType.Vehicle.CAR1.r(10);
        while (FuelType.Vehicle.CAR1.gettC()-FuelType.Vehicle.CAR1.getcL() > 0) {
            System.out.print("Заправить литров: ");
            int l = scanner.nextInt();
            double cost = FuelType.Vehicle.Station.Manager.Pump.P.fBL(FuelType.Vehicle.CAR1, l);
            System.out.println("Заправка: " + cost + ", теперь в баке: " + FuelType.Vehicle.CAR1.getcL());
            System.out.println("Выручка: " + FuelType.Vehicle.Station.Manager.r());
        }
    }
}
