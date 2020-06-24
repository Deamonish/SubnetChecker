import org.apache.commons.lang3.Range;
import org.apache.commons.net.util.SubnetUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Test {

    static Integer foundIpsCounter = 0;

    public static void main(String[] args) throws FileNotFoundException {
        List<Range<Integer>> ranges = new ArrayList<>(6000);
        System.out.println(new File("src/main/resources/datacenters.netset").getAbsolutePath());

        Scanner scanner = new Scanner(new File("src/main/resources/datacenters.netset"));
        while (scanner.hasNextLine()) {
            String subnetIp = scanner.nextLine();
            try {
                SubnetUtils subnetUtils = new SubnetUtils(subnetIp);
                String highAddress = subnetUtils.getInfo().getHighAddress();
                String lowAddress = subnetUtils.getInfo().getLowAddress();

                int low = ipToInteger(lowAddress);
                int high = ipToInteger(highAddress);

                ranges.add(Range.between(low, high));
            } catch (IllegalArgumentException ex) {
                System.out.println("It seems, that file contained incorrect subnet address: " + subnetIp);
            }
        }

        Test.findIpsInRange(new File("src/main/resources/second.txt"), ranges);
        Test.findIpsInRange(new File("src/main/resources/third.txt"), ranges);
        System.out.println("Start processing 500k ips.");
        long start = System.currentTimeMillis();
        Test.findIpsInRange(new File("src/main/resources/testSampleFileWith500000ips.txt"), ranges);
        System.out.println("Finished. Time: " + (start - System.currentTimeMillis()) + " mc");
        System.out.println(foundIpsCounter + " ips found in Blacklist");

    }

    public static void findIpsInRange(File fileWithIps, List<Range<Integer>> ranges) throws FileNotFoundException {
        Scanner playerIps = new Scanner(fileWithIps);
        while (playerIps.hasNextLine()) {
            String playerIp = playerIps.nextLine();
            final int ip = ipToInteger(playerIp);
            Optional<Range<Integer>> any = ranges.stream().filter((range) -> range.contains(ip)).findAny();
            if (any.isPresent()) {
                System.out.println("IP: " + playerIp + " is found in blacklist");
                foundIpsCounter++;
            }
        }
    }

    public static int ipToInteger(String ipAddress) {

        int result = 0;

        String[] ipAddressInArray = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {

            int ip = Integer.parseInt(ipAddressInArray[3 - i]);

            //left shifting 24,16,8,0 and bitwise OR

            //1. 192 << 24
            //1. 168 << 16
            //1. 1   << 8
            //1. 2   << 0
            result |= ip << (i * 8);

        }

        return result;
    }
}
