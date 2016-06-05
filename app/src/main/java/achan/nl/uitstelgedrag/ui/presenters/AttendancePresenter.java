package achan.nl.uitstelgedrag.ui.presenters;

import java.util.List;

import achan.nl.uitstelgedrag.domain.models.Timestamp;

/**
 * Created by Etienne on 29-5-2016.
 */
public interface AttendancePresenter {

    void checkIn(Timestamp timestamp, AttendancePresenterImpl.Callback onSuccess, AttendancePresenterImpl.Callback onError);
    Timestamp checkOut(Timestamp timestamp);
    List<Timestamp> viewAttendance();

}
