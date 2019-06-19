class Aircraft(object):

    def __init__(self, number_of_passengers, name):
        self._number_of_passenger = number_of_passengers
        self._name = name

    def get_number_of_passengers(self):
        return self._number_of_passenger

    def get_name(self):
        return self._name


class IntercontinentalAircraft(Aircraft):
    consumption_per_km_per_passenger = 0.25

    consumption_per_km_per_tonne_cargo = 2

    def __init__(self, number_of_passengers, name, cargo_load):
        Aircraft.__init__(self, number_of_passengers, name)
        self.__cargo_load = cargo_load

    def calculate_amount_of_fuel(self, km):
        passenger_consumption = IntercontinentalAircraft.consumption_per_km_per_passenger * self._number_of_passenger * km
        cargo_consumption = IntercontinentalAircraft.consumption_per_km_per_tonne_cargo * self.__cargo_load * km
        return passenger_consumption + cargo_consumption

    @property
    def manifest(self):
        return f"Intercontinental flight {self._name}: passenger count {self._number_of_passenger}, cargo load {self.__cargo_load}"


class ShortHaulAircraft(Aircraft):
    consumption_per_km_per_passenger = 0.1

    serial_number_counter = 0

    def __init__(self, number_of_passengers, name):
        Aircraft.__init__(self, number_of_passengers, name)
        self.__serial_number = ShortHaulAircraft.construct_new_serial_number()

    def calculate_amount_of_fuel(self, km):
        return ShortHaulAircraft.consumption_per_km_per_passenger * km * self.get_number_of_passengers()

    def get_serial_number(self):
        return self.__serial_number

    @property
    def manifest(self):
        return f"Short haul flight serial number {self.__serial_number}, name {self._name}: passenger count {self._number_of_passenger}"

    @staticmethod
    def construct_new_serial_number():
        serial_number = ShortHaulAircraft.serial_number_counter
        ShortHaulAircraft.serial_number_counter += 1
        return serial_number


class ControlTower(object):

    def __init__(self):
        self.__planes = []

    def add_aircraft(self, aircraft):
        self.__planes.append(aircraft)

    def get_manifests(self):
        return [aircraft.manifest for aircraft in self.__planes]


if __name__ == '__main__':
    intercontinental_flight = IntercontinentalAircraft(500, "Boeing-747", 100)
    short_haul_flight = ShortHaulAircraft(110, "Airbus-A220")
    short_haul_flight2 = ShortHaulAircraft(85, "Airbus-A220")

    assert short_haul_flight.get_serial_number() == 0
    assert short_haul_flight2.get_serial_number() == 1

    assert short_haul_flight.get_number_of_passengers() == 110
    assert short_haul_flight.get_name() == "Airbus-A220"

    assert intercontinental_flight.get_number_of_passengers() == 500
    assert intercontinental_flight.get_name() == "Boeing-747"

    assert intercontinental_flight.calculate_amount_of_fuel(10000) == 3250000.
    assert short_haul_flight.calculate_amount_of_fuel(250) == 2750.

    assert intercontinental_flight.manifest == "Intercontinental flight Boeing-747: passenger count 500, cargo load 100"
    assert short_haul_flight2.manifest == "Short haul flight serial number 1, name Airbus-A220: passenger count 85"

    tower = ControlTower()
    tower.add_aircraft(intercontinental_flight)
    tower.add_aircraft(short_haul_flight)
    tower.add_aircraft(short_haul_flight2)

    air_traffic_report = tower.get_manifests()
    for aircraft in air_traffic_report:
        print(aircraft)

    # prints:
    # Intercontinental flight Boeing-747: passenger count 500, cargo load 100
    # Short haul flight serial number 0, name Airbus-A220: passenger count 110
    # Short haul flight serial number 1, name Airbus-A220: passenger count 85
