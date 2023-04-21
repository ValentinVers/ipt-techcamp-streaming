
using Confluent.Kafka;
using Azure.Core;
using System;
using Azure.Identity;

using System.Text.Json;
using System.Text.Json.Serialization;


namespace KafkaProducer
{

    public class DemoObject
    {
        public string id { get; set; }
        public int count { get; set; }
        public DateTime date { get; set; }
        public string content { get; set; }
    }

    class DemoRelated
    {
        public string parent_id { get; set; }
        public int index { get; set; }
        public int value { get; set; }
    }

    public class Program
    {
        private static string eventHubNamespace = "techcamp";


        private static string eventHubNamespaceFQDNwithPort = $"{eventHubNamespace}.servicebus.windows.net:9093";

        //in kafka world this is the topic in event hub is the event hub name under the namespace
        private static string topicName = "demo";

        static void OauthCallback(IClient client, string cfg)
        {
            try
            {
                Console.WriteLine("Getting auth token");

                DefaultAzureCredentialOptions secretOptions = new DefaultAzureCredentialOptions();
                DefaultAzureCredential secretCredential = new DefaultAzureCredential(secretOptions);

                var tokenRequestContext = new TokenRequestContext(new string[] { $"https://{eventHubNamespace}.servicebus.windows.net/.default" });

                var accessToken = secretCredential.GetToken(tokenRequestContext);

                client.OAuthBearerSetToken(accessToken.Token, accessToken.ExpiresOn.ToUnixTimeMilliseconds(), null);
            }
            catch (Exception e)
            {
                Console.WriteLine($"ERROR {e.Message}");

                client.OAuthBearerSetTokenFailure(e.ToString());
            }
        }

        static async Task Main(string[] args)
        {
            Console.WriteLine("Hello Event Hub Kafka Client");

            var config = new ProducerConfig
            {
                BootstrapServers = eventHubNamespaceFQDNwithPort,
                SaslMechanism = SaslMechanism.OAuthBearer,
                SecurityProtocol = SecurityProtocol.SaslSsl,
                BrokerVersionFallback = "0.10.0.0",
                ApiVersionRequestTimeoutMs = 10,
                Debug = "security,broker,protocol"
            };

            var configKey = new ProducerConfig
            {
                BootstrapServers = eventHubNamespaceFQDNwithPort,
                SecurityProtocol = SecurityProtocol.SaslSsl,
                SaslMechanism = SaslMechanism.Plain,
                SaslUsername = "$ConnectionString",
                SaslPassword = Environment.GetEnvironmentVariable("CONNECTIONSTRING")
            };

            var producerBuilder = new ProducerBuilder<string, string>(configKey);

            //IProducer<Null, string> producer = producerBuilder.SetOAuthBearerTokenRefreshHandler(OauthCallback).Build();
            IProducer<string, string> producer = producerBuilder.Build();


            Random rnd = new Random();

            Parallel.For(0, 1000, async i => {
                {
                    Thread.Sleep(rnd.Next(500, 1000));

                    DemoObject obj = new DemoObject
                    {
                        id = Guid.NewGuid().ToString(),
                        count = rnd.Next(0, 20),
                        date = DateTime.Now,
                        content = DateTime.Now.ToString("dd-MM-yyyy HH:mm:ss.FFF")
                    };

                    var msg = new Message<string, string>
                    {
                        Key = obj.id,
                        Value = JsonSerializer.Serialize(obj)
                    };

                    // publishes the message to Event Hubs
                    var resultPromise = producer.ProduceAsync(topicName, msg);

                    for (int y = 0; y < obj.count; y++)
                    {
                        Thread.Sleep(rnd.Next(0, 500));
                        var related = new DemoRelated
                        {
                            parent_id = obj.id,
                            index = y,
                            value = rnd.Next()
                        };
                        var msg2 = new Message<string, string>
                        {
                            Key = related.parent_id,
                            Value = JsonSerializer.Serialize(related),

                        };

                        producer.ProduceAsync("demo-related", msg2);
                    }


                    var result = await resultPromise;
                    Console.WriteLine($"Message {result.Value} sent to partition {result.TopicPartition} with result {result.Status}");
                }

                Console.WriteLine("Producer complete");
            });
            
        }

    }
}

