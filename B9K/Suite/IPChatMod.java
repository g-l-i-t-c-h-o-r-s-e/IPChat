package B9K.Suite;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

@Mod(
   modid = "IPChat",
   version = "1.0",
   name = "IPChat",
   canBeDeactivated = true,
   acceptedMinecraftVersions = "[1.8.9]"
)
public class IPChatMod {
   public static final String MODID = "IPChat";
   public static final String MODNAME = "IPChat";
   public static final String VERSION = "1.0";
   private IPChatMod.eUdpClient sock;
   private static ModContainer thisMod;

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(new IPChatMod.B9KListener());
      ClientCommandHandler.instance.func_71560_a(new IPChatMod.B9KCmd());
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      this.PrintLine("");
      this.PrintLine("");
      this.PrintLine("");
      this.PrintLine("");
      this.PrintLine("ChatSocket Initialization");
      this.prepSock();
   }

   private void prepSock() {
      this.sock = new IPChatMod.eUdpClient();
      this.sock.port = 12345;
      this.sock.hostname = "127.0.0.1";
      this.sock.init();
      this.sock.startListening();
   }

   private void HandleDatagram(String d) {
      this.PrintLine("STR: " + d);
      String[] datagram = d.split(Pattern.quote(this.getSep()), 2);
      if (datagram.length < 2) {
         this.PrintLine("MALFORMED DATAGRAM {" + d + "}. DROPPED.");
      } else {
         String verb = datagram[0];
         String param = datagram[1];
         this.HandleLine(verb, param);
      }
   }

   private void HandleLine(String verb, String param) {
      this.ll("DATAGRAM: VERB=" + verb + " PARAM=" + param + "");
      if (verb.equals("<HELO>")) {
         this.WriteLine("[B9K-UDP][*] " + param);
      } else if (verb.equals("<CHT>")) {
         this.SendLine(param);
      } else if (verb.equals("<LCL>")) {
         this.WriteLine(param);
      } else if (verb.equals("<STD>")) {
         this.PrintLine(param);
      } else if (verb.equals("<CMD>")) {
         this.HandleCommand(param);
      } else {
         this.ll("UNKNOWN VERB: " + verb + " [" + param + "]");
      }
   }

   private void HandleCommand(String param) {
      if (param.equals("PING")) {
         this.SendUdp("HELO", "IPChat v1.0 salutes you!");
      }
   }

   private void SendRawUdp(String text) {
      this.PrintLine("UDP -> " + text);
      this.sock.SendDatagram(text, "127.0.0.1", 54321);
   }

   private void Mex(String chatMex) {
      this.SendUdp("MEX", chatMex);
   }

   private String getSep() {
      char c = "§".toCharArray()[0];
      return "{" + c + "}";
   }

   private void SendUdp(String verb, String param) {
      this.SendRawUdp("<" + verb + ">" + this.getSep() + param);
   }

   private void SendLine(String text) {
      if (this.inGameChatAvailable()) {
         Minecraft.func_71410_x().field_71439_g.func_71165_d(text);
      } else {
         this.PrintLine("{Player==null!} (WL) -> " + text);
      }
   }

   private void WriteLine(String text) {
      if (this.inGameChatAvailable()) {
         Minecraft.func_71410_x().field_71439_g.func_146105_b(new ChatComponentText(text));
      } else {
         this.PrintLine("{Player==null!} (WL) -> " + text);
      }
   }

   private void PrintLine(String text) {
      System.out.println(text);
   }

   private void ll(String text) {
      this.PrintLine(text);
   }

   private boolean inGameChatAvailable() {
      return Minecraft.func_71410_x().field_71439_g != null;
   }

   class B9KCmd extends CommandBase {
      public String func_71517_b() {
         return "B9K";
      }

      public List<String> func_71514_a() {
         List ret = new ArrayList();
         ret.add("b9k");
         ret.add("b9K");
         ret.add("B9k");
         return ret;
      }

      public String func_71518_a(ICommandSender sender) {
         return "Random test & things.";
      }

      public void func_71515_b(ICommandSender sender, String[] args) throws CommandException {
         IPChatMod.this.ll("Active Mods List:");

         for (ModContainer m : Loader.instance().getActiveModList()) {
            IPChatMod.this.ll(m.getModId());
         }
      }

      public int func_82362_a() {
         return 0;
      }
   }

   public class B9KListener {
      @SubscribeEvent
      public void onChatMessage(ClientChatReceivedEvent event) {
         String text = event.message.func_150260_c();
         IPChatMod.this.PrintLine("Chat Message: " + event.message.func_150260_c());
         IPChatMod.this.Mex(text);
      }

      @SubscribeEvent
      public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
         IPChatMod.this.SendUdp("HELO", "IPChat-1.0 connecting to a server!");
      }
   }

   class NullCmd extends CommandBase {
      public String func_71517_b() {
         return "null";
      }

      public String func_71518_a(ICommandSender sender) {
         return "This command does nothing.";
      }

      public void func_71515_b(ICommandSender sender, String[] args) throws CommandException {
      }

      public int func_82362_a() {
         return 0;
      }
   }

   class StealthModeHandler {
      String targetModId = null;
      boolean enabled = true;

      StealthModeHandler(String id) {
         this.targetModId = id;
      }

      @SubscribeEvent
      public void hideMod(ClientConnectedToServerEvent event) {
         IPChatMod.this.ll("Hiding IPChat...");
         List<ModContainer> x = this.getActiveModList();
         if (x == null) {
            IPChatMod.this.ll("Reflection failed while hiding mod. Aborting connection.");
            event.manager.func_150718_a(new ChatComponentText("FAIL"));
         }

         x.removeIf(new Predicate<ModContainer>() {
            public boolean test(ModContainer p) {
               if (p.getModId().equalsIgnoreCase("IPChat")) {
                  IPChatMod.thisMod = p;
                  return true;
               } else {
                  return false;
               }
            }
         });
      }

      private void unHideMod() {
      }

      List<ModContainer> getActiveModList() {
         Loader t = Loader.instance();
         LoadController z = null;
         List<ModContainer> x = null;

         try {
            z = (LoadController)this.getInstanceValue(t, "modController");
         } catch (SecurityException var10) {
            var10.printStackTrace();
         } catch (NoSuchFieldException var11) {
            var11.printStackTrace();
         } catch (ClassNotFoundException var12) {
            var12.printStackTrace();
         } catch (IllegalArgumentException var13) {
            var13.printStackTrace();
         } catch (IllegalAccessException var14) {
            var14.printStackTrace();
         }

         try {
            x = (List<ModContainer>)this.getInstanceValue(z, "activeModList");
         } catch (SecurityException var5) {
            var5.printStackTrace();
         } catch (NoSuchFieldException var6) {
            var6.printStackTrace();
         } catch (ClassNotFoundException var7) {
            var7.printStackTrace();
         } catch (IllegalArgumentException var8) {
            var8.printStackTrace();
         } catch (IllegalAccessException var9) {
            var9.printStackTrace();
         }

         return x;
      }

      @SubscribeEvent
      public void onClientDisconnectionFromServerEvent(ClientDisconnectionFromServerEvent event) {
         this.unHideMod();
      }

      public Object getInstanceValue(Object classInstance, String fieldName) throws SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
         Field field = classInstance.getClass().getDeclaredField(fieldName);
         field.setAccessible(true);
         return field.get(classInstance);
      }
   }

   class eUdpClient {
      private String hostname = "localhost";
      private int port = 12345;
      private InetAddress host;
      private DatagramSocket socket;
      public static final String encoding = "UTF-16LE";
      public Thread listenThread = this.mkt();
      private boolean ts = false;

      public void init() {
         try {
            this.host = InetAddress.getByName(this.hostname);
            this.socket = new DatagramSocket(this.port);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

      public void SendDatagram(String text, String target_host, int target_port) {
         try {
            byte[] buf = text.getBytes("UTF-16LE");
            int buflen = buf.length;
            DatagramPacket packet = new DatagramPacket(buf, buflen, InetAddress.getByName(target_host), target_port);
            this.socket.send(packet);
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      private Thread mkt() {
         return new Thread(new Runnable() {
            @Override
            public void run() {
               eUdpClient.this.listen2();
            }
         });
      }

      public void startListening() {
         if (!this.ts) {
            this.ts = true;
            this.listenThread.start();
         }
      }

      public void listen2() {
         IPChatMod.this.PrintLine("UDP Listening on port " + String.valueOf(this.port));

         while (true) {
            byte[] rBuffer = new byte[1024];

            try {
               DatagramPacket packet = new DatagramPacket(rBuffer, rBuffer.length);
               this.socket.receive(packet);
               if (this.socket.getLocalPort() == packet.getPort()) {
                  IPChatMod.this.PrintLine("Packet from self ignored.");
               } else {
                  String outString = this.getStringW(packet.getData());
                  IPChatMod.this.HandleDatagram(outString);
               }
            } catch (UnsupportedEncodingException var4) {
               var4.printStackTrace();
            } catch (IOException var5) {
               var5.printStackTrace();
            }
         }
      }

      private String getStringW(byte[] data) throws UnsupportedEncodingException {
         int remainingBytes = data.length;
         String outBuffer = "";

         for (int i = 0; i < data.length; i += 2) {
            if (remainingBytes < 2) {
               return outBuffer;
            }

            byte b1 = data[i];
            byte b2 = data[i + 1];
            remainingBytes = data.length - i;
            if (b1 == 0 && b2 == 0) {
               return outBuffer;
            }

            String ss = new String(new byte[]{b1, b2}, "UTF-16LE");
            outBuffer = outBuffer + ss;
         }

         return outBuffer;
      }
   }
}
