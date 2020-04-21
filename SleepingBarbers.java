package sleepingbarber;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SleepingBarbers extends Thread {
	public static final int CHAIRS = 3;

	//public static final long BARBER_TIME = 5000;

	//private static final long CUSTOMER_TIME = 1500;

	//public static final long WAIT_TIME = 0;

	private static final int BARBERS_NUMBER = 2;
	
	private static final int CUSTOMERS_NUMBER = 10;
	
	private static final int THREADPOOLCOUNT = BARBERS_NUMBER + CUSTOMERS_NUMBER;

	public static BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(
			CHAIRS);

	private ExecutorService executor;

	class Customer extends Thread {
		int iD;
		boolean notCut = true;

		BlockingQueue<Integer> queue = null;

		public Customer(int i, BlockingQueue<Integer> queue) {
			iD = i;
			this.queue = queue;
		}

		public void run() {
			while (true) { // as long as the customer is not cut he is in the queue or if not enough chairs he will leave
				try {
					//System.out.println("checking for customer thread id:" + Thread.currentThread().getId());
					this.queue.add(this.iD);
					this.getHaircut(); // getting a chair for haircut
				} catch (IllegalStateException e) {

					System.out.println("Customer "
							+ this.iD + " has left the barbershop as there are no free seats available. The thread id for this is ------ " + Thread.currentThread().getId());
				}
				break;
			}
		}

		// take a seat
		public void getHaircut() {
			System.out.println("Customer " + this.iD + " has entered and taken a chair, the thread id for this is ----- " + Thread.currentThread().getId());
		}
	}

	class Barber extends Thread {
		BlockingQueue<Integer> queue = null;
		private String name;
		

		public Barber(BlockingQueue<Integer> queue, String name) {
			this.name = name;
			this.queue = queue;
		}

		public void run() {
			Random randno = new Random();
			long r = (long) randno.nextGaussian();
			long WAIT_TIME = 30; //WAIT_TIME in seconds, this is the polling time for which the barber waits and assumes no customers in queue, 
								 //exits the execution thread after this pollling time
			
			while (true) {
				try {
					//System.out.println("this is to check the current barber thread id:"+Thread.currentThread().getId());
					Integer i = this.queue.poll((long) WAIT_TIME,
							TimeUnit.SECONDS);
					if (i == null)
					{
						System.out.println("the barber:"+ name +" is sleeping as no customers are waiting, the thread id for this is ---- " + Thread.currentThread().getId());
						break; // barber slept for long time (WAIT_TIME) meaning no more clients in the queue, so done
						//System.exit(0);
					}
					this.cutHair(i,r); // cutting

				} catch (InterruptedException e) {
				}
			}
		}

		public void cutHair(Integer i,double r) {
			System.out.println("The barber " + this.name
					+ " has been woken up and is cutting hair for customer number " + i + ",the thread id for this is ----- "+ Thread.currentThread().getId());
			try {
				TimeUnit.SECONDS.sleep((long)Math.abs(r));
			} catch (InterruptedException ex) {
			}
		}
	}

	public static void main(String args[]) {
		SleepingBarbers barberShop = new SleepingBarbers();
		barberShop.start(); // starting the barbershop here
	}

	public void run() {
		 
		executor = Executors.newFixedThreadPool(THREADPOOLCOUNT);
		for (int i = 1; i <= BARBERS_NUMBER; i++)
			executor.submit(new Barber(
					SleepingBarbers.queue, Integer.toString(i)));

		// creating CUSTOMER_NUMBER + 1 to check for customer exiting condition without the availability of a chair
		
		for (int i = 1; i < 11; i++) {
			Customer aCustomer = new Customer(i,
					SleepingBarbers.queue);
			executor.submit(aCustomer);
			try {
				Random randomno = new Random();
				double RValue = randomno.nextGaussian();
				//Thread.sleep((long)Math.abs(RValue));
				TimeUnit.SECONDS.sleep((long)Math.abs(RValue));
				//Thread.sleep(ThreadLocalRandom.current().nextInt(100, 1000 + 100));
				//sleep(CUSTOMER_TIME);
			} catch (InterruptedException ex) {
			}
			;
		}/*try{sleep(15000);}catch (InterruptedException e) {};
		System.exit(0);*/
	}
}