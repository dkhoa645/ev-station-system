package com.group3.evproject.repository;

import com.group3.evproject.entity.Booking;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    //lay danh sach booking theo nguoi dung
    List<Booking> findByUser(User user);

    //lay danh sach booking theo tram sac
    List<Booking> findByStation(ChargingStation station);

    //lay danh booking theo user + status
    List<Booking> findByUserAndStatus(User user, Booking.BookingStatus status);

    //tim booking con hieu luc (tranh trung tgian)
    Optional<Booking> findFirstByUserAndStatus(User user, Booking.BookingStatus status);

    //tim booking theo user va ID (user chi xem dc booking cua minh)
    Optional<Booking> findByIdAndUser(Long id, User user);
}