package com.group3.evproject.repository;
import com.group3.evproject.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

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
}
