package achan.nl.uitstelgedrag.persistence;

import java.util.Date;

import achan.nl.uitstelgedrag.domain.models.Dayplan;

/**
 * Created by Etienne on 17-8-2016.
 */
public interface DayplanRepository extends Repository<Dayplan> {

    Dayplan get(Date date);

}
