package com.group3.evproject.repository;

import com.group3.evproject.entity.Booking;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

<<<<<<< HEAD
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //lay danh sach booking theo nguoi dung
    List<Booking> findByUser(User user);
    //lay danh sach booking theo tram sac
    List<Booking> findByChargingStation(ChargingStation chargingStation);
    //lay danh booking theo user + status
    List<Booking> findByUserAndStatus(User user, Booking.BookingStatus status);
    //tim booking con hieu luc (tranh trung tgian)
    Optional<Booking> findFirstByUserAndStatus(User user, Booking.BookingStatus status);
    //tim booking theo user va ID (user chi xem dc booking cua minh)
    Optional<Booking> findByIdAndUser(Long id, User user);
=======
public interface BookingRepository extends  JpaRepository<Booking, Integer> {
    //lay tat ca booking cua 1 user
//    List<Booking> findByUserId(Integer userId);

    //lay tat ca booking cua 1 tram sac
    List<Booking> findByStationId(Integer stationId);

    //lay tat ca booking cua trang thai
    List<Booking> findByStatus(String status);

    //lay tat ca booking cua 1 vehicle
//    List<Booking> findByVehicleId(Integer vehicleId);

    //lay tat ca booking cua 1 spot
    List<Booking> findBySpotId(Integer spotId);

    //lay tat ca booking trong khoang thoi gian
    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    //tim booking dang active (dang dien ra) cua 1 spot
    List<Booking> findBySpotIdAndStatus(Integer spotId, String status);

    //kiem tra user co booking dang sac khong
//    List<Booking> findByUserIdAndStatus(Integer userId, String status);
>>>>>>> 913dc038178c8d2e3085d4faba3d7a05e32badbd
}
