/**
 * 1. Run parser.java with the following parameters:
 *   --accesslog=d:\\programming\\interviews\\WalletHub\\access.log --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200
 */

select ip_address, count(ip_address) from log_entries
where start_date between '2017-01-01 15:00:00' and '2017-01-01 15:59:59'
 group by ip_address having count(ip_address) > 200 order by 2 desc

/**
 * 2. Run parser.java with the following parameters:
 *   --accesslog=d:\\programming\\interviews\\WalletHub\\access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500
 */
select ip_address, count(ip_address) from log_entries
where start_date between '2017-01-01 00:00:00' and '2017-01-02 09:59:59'
group by ip_address having count(ip_address) > 500 order by 2 desc;

/**
 * 3. Get statistics on the given IP address:
 */
select * from log_entries where ip_address='192.168.206.141';
