# java-explore-with-me
## Application where you can add different events and another users can become participant of these events.

Spring Boot, Hibernate, Maven, H2, Lombok

Application has three sides:
1. public
2. private
3. admin

## Public
All users can see any event by ID or events by different parameters like:
- events containing text in description or annotation,
- paid or not paid events,
- events by certain category
- events with event date which between certain dates

All users can see compilations events or certain compilation event by ID.

All users can see all categories of events or certain category by ID.

All users can see all published comments on the event.

## Private
Only authorized users can add events, can modify or cancel evens, can confirm or reject user's request for participation.
Authorized users can leave a request for participation in the event.

Authorized users can live comment on event, but user should be participant of event. Also, user can change, delete comment.
New comment automatically get status PENDING, after changing comment again has status PENDING till Admin confirm or reject it.


## Admin
Administrator can add new categories of events, modify or cancel currently categories.
Admin can add new compilations of events, modify, delete. Also, admin can pin compilations on main page.
Admin can public event or rejected, and can modify events.

Admin can see comments with different status (PENDING, REJECTED or PUBLISHED). All comments should be published or rejected by Admin.



Link to Pull requests https://github.com/EvgeniyaKuzmina/java-explore-with-me/pull/1 
