import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author sy
 * @date 2022/2/10 10:35
 */

public class IOAndNIOTest {

    /**
     * 非直接缓冲区
     * @throws IOException
     */
    @Test
    public void test001() throws IOException {
        File file = new File("d://2.exe");
        if (file.exists()) {
            file.deleteOnExit();
        }
        long startTime = System.currentTimeMillis();

        // 读入流
        FileInputStream fis = new FileInputStream("d://1.exe");
        // 写入流
        FileOutputStream fos = new FileOutputStream("d://2.exe");

        // 创建管道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        // 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        while (inChannel.read(buf) != -1) {
            // 开启读取模式
            buf.flip();
            // 将数据写入通道
            outChannel.write(buf);
            buf.clear();
        }

        // 关闭通道、关闭连接
        inChannel.close();
        outChannel.close();
        fos.close();
        fis.close();
        long endTime = System.currentTimeMillis();
        System.out.println("操作非直接缓冲区耗时时间：" + (endTime - startTime));
    }


    /**
     * 直接缓冲区
     * @throws IOException
     */
    @Test
    public void test002() throws IOException {
        File file = new File("d://2.exe");
        if (file.exists()) {
            file.deleteOnExit();
        }
        long startTime = System.currentTimeMillis();

        //创建管道
        FileChannel inChannel = FileChannel.open(Paths.get("d://1.exe"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("d://2.exe"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //定义映射文件
        MappedByteBuffer inMappedByte = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedByte = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        //直接对缓冲区进行操作
//        byte[] dsf = new byte[inMappedByte.limit()];
        byte[] dsf = new byte[1024];
        inMappedByte.get(dsf);
        outMappedByte.put(dsf);

        inChannel.close();
        outChannel.close();
        long endTime = System.currentTimeMillis();
        System.out.println("操作直接缓冲区耗时时间：" + (endTime - startTime));
    }

    /**
     * 分散聚合
     * @throws IOException
     */
    @Test
    public void  test003() throws IOException{
        File file = new File("d://2.exe");
        if (file.exists()) {
            file.deleteOnExit();
        }
        long startTime = System.currentTimeMillis();

        // 分散读取
        RandomAccessFile inRa = new RandomAccessFile("d://1.exe","rw");
        // 分散聚合
        RandomAccessFile outRa = new RandomAccessFile("d://2.exe", "rw");

        // 获取通道
        FileChannel inChannel = inRa.getChannel();
        FileChannel outChannel = outRa.getChannel();

//        // 分散读取，就是使用多个buffer来装数据，可继续增加，值为名称缓冲区大小
        ByteBuffer by1 = ByteBuffer.allocate(20);
        ByteBuffer by2 = ByteBuffer.allocate(500);
        ByteBuffer[] byteBuffers = {by1, by2};
//        outChannel.transferFrom(inChannel, 0, inChannel.size());
        while (inChannel.read(byteBuffers) != -1) {
            by1.flip();
            by2.flip();
            outChannel.write(byteBuffers);
            by1.clear();
            by2.clear();
        }

        long endTime = System.currentTimeMillis();

        outChannel.close();
        inChannel.close();
        outRa.close();
        inRa.close();
        System.out.println("分散读取、聚合耗时时间：" + (endTime - startTime));

    }


    @Test
    public void test004() throws IOException {
        File file = new File("d://2.exe");
        if (file.exists()) {
            file.deleteOnExit();
        }

        long startTime = System.currentTimeMillis();
        // 创建流对象
        InputStream is = new FileInputStream("d://1.exe");
        OutputStream os = new FileOutputStream("d:/2.exe");

        // 创建字节缓冲流
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);

        // 创建字节组大小
        byte[] bytes = new byte[1024];
        while (bis.read(bytes) != -1) {
            // 将数据写出
            bos.write(bytes);
            // 强制刷新缓冲流数据
            bos.flush();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("字节缓冲流耗时时间："+(endTime-startTime));
        bos.close();
        bis.close();
        os.close();
        is.close();
    }
}
