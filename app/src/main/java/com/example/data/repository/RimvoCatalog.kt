package com.example.data.repository

import com.example.R
import com.example.data.model.InstallmentTenure
import com.example.data.model.Product
import com.example.data.model.ProductCategory

object RimvoCatalog {
    val products: List<Product> = listOf(
        Product(
            id = "wh-walton-43-tv",
            nameEn = "Walton 43\" Frameless 4K Google Smart TV",
            nameBn = "ওয়ালটন ৪৩\" ফ্রেমলেস ৪কে গুগল স্মার্ট টিভি",
            brand = "Walton",
            category = ProductCategory.ELECTRONICS,
            cashPriceBdt = 32900,
            originalPriceBdt = 36500,
            rating = 4.8,
            reviewCount = 142,
            imageRes = R.drawable.img_electronics_tv,
            specsEn = listOf(
                "4K UHD HDR10 Frameless Display",
                "Official Google TV OS with Voice Remote",
                "Dolby Digital 20W Dual Stereo Speakers",
                "2 Years Official Walton Panel Warranty"
            ),
            specsBn = listOf(
                "৪কে ইউএইচডি এইচডিআর১০ ফ্রেমলেস ডিসপ্লে",
                "অফিশিয়াল গুগল টিভি ও ভয়েস রিমোট",
                "ডলবি ডিজিটাল ২০ ওয়াট ডুয়াল স্পিকার",
                "২ বছরের অফিশিয়াল ওয়ালটন প্যানেল ওয়ারেন্টি"
            ),
            maxTenure = InstallmentTenure.TWELVE_MONTHS,
            badgeEn = "0% 3M BNPL",
            badgeBn = "০% ৩ মাসের কিস্তি"
        ),
        Product(
            id = "rh-grocery-family-mega",
            nameEn = "Rimvo Family Monthly Grocery Mega Combo",
            nameBn = "রিমভো ফ্যামিলি মাসিক গ্রোসারি মেগা কম্বো",
            brand = "Rimvo Fresh",
            category = ProductCategory.GROCERY,
            cashPriceBdt = 4850,
            originalPriceBdt = 5400,
            rating = 4.9,
            reviewCount = 318,
            imageRes = R.drawable.img_grocery_combo,
            specsEn = listOf(
                "Miniket Premium Rice 25kg + Chinigura 2kg",
                "Rupchanda Soybean Oil 5L + Masoor Dal 3kg",
                "Ispahani Mirzapore Tea 400g + Spice Pack",
                "Same-day Dhaka & Next-day Nationwide Delivery"
            ),
            specsBn = listOf(
                "প্রিমিয়াম মিনিকেট চাল ২৫ কেজি + চিনিগুঁড়া ২ কেজি",
                "রূপচাঁদা সয়াবিন তেল ৫ লিটার + মসুর ডাল ৩ কেজি",
                "ইস্পাহানি মির্জাপুর চা ৪০০ গ্রাম + মসলা প্যাক",
                "ঢাকায় সেম-ডে এবং সারাদেশে নেক্সট-ডে ডেলিভারি"
            ),
            maxTenure = InstallmentTenure.THREE_MONTHS,
            badgeEn = "Popular Combo",
            badgeBn = "জনপ্রিয় কম্বো"
        ),
        Product(
            id = "vs-vision-fridge-252",
            nameEn = "Vision 252L Frost-Free Inverter Refrigerator",
            nameBn = "ভিশন ২৫২ লিটার ফ্রস্ট-ফ্রি ইনভার্টার ফ্রিজ",
            brand = "Vision",
            category = ProductCategory.APPLIANCES,
            cashPriceBdt = 34500,
            originalPriceBdt = 38000,
            rating = 4.7,
            reviewCount = 96,
            imageRes = R.drawable.img_home_appliance,
            specsEn = listOf(
                "60% Energy Saving Digital Inverter Compressor",
                "Anti-Bacterial Gasket & Tempered Glass Shelves",
                "Built-in Voltage Stabilizer (135V–265V)",
                "10 Years Compressor Official Warranty"
            ),
            specsBn = listOf(
                "৬০% বিদ্যুৎ সাশ্রয়ী ডিজিটাল ইনভার্টার কম্প্রেসর",
                "অ্যান্টি-ব্যাকটেরিয়াল গ্যাস্কেট ও টেম্পার্ড গ্লাস শেল্ফ",
                "বিল্ট-ইন ভোল্টেজ স্ট্যাবিলাইজার (১৩৫-২৬৫ ভোল্ট)",
                "১০ বছরের কম্প্রেসর অফিশিয়াল ওয়ারেন্টি"
            ),
            maxTenure = InstallmentTenure.TWELVE_MONTHS,
            badgeEn = "Save ৳3,500",
            badgeBn = "৳৩,৫০০ ছাড়"
        ),
        Product(
            id = "sg-singer-microwave-25l",
            nameEn = "Singer 25L Digital Grill Microwave Oven",
            nameBn = "সিঙ্গার ২৫ লিটার ডিজিটাল গ্রিল মাইক্রোওয়েভ ওভেন",
            brand = "Singer",
            category = ProductCategory.KITCHEN,
            cashPriceBdt = 13900,
            originalPriceBdt = 15500,
            rating = 4.6,
            reviewCount = 74,
            imageRes = R.drawable.img_home_appliance,
            specsEn = listOf(
                "25L Family Capacity with Stainless Cavity",
                "10 Auto-Cook Bangladeshi & Continental Menus",
                "Defrost by Weight & Child Safety Lock",
                "2 Years Singer Home Service Warranty"
            ),
            specsBn = listOf(
                "২৫ লিটার ফ্যামিলি ক্যাপাসিটি ও স্টেইনলেস ক্যাভিটি",
                "১০টি অটো-কুক দেশি ও কন্টিনেন্টাল মেনু",
                "ওজন অনুযায়ী ডিফ্রস্ট এবং চাইল্ড সেফটি লক",
                "২ বছরের সিঙ্গার হোম সার্ভিস ওয়ারেন্টি"
            ),
            maxTenure = InstallmentTenure.SIX_MONTHS,
            badgeEn = "0% 3M BNPL",
            badgeBn = "০% ৩ মাসের কিস্তি"
        ),
        Product(
            id = "my-miyako-blender-3in1",
            nameEn = "Miyako 750W Heavy-Duty 3-in-1 Mixer Grinder",
            nameBn = "মিয়াকো ৭৫০ ওয়াট হেভি-ডিউটি ৩-ইন-১ মিক্সার গ্রাইন্ডার",
            brand = "Miyako",
            category = ProductCategory.KITCHEN,
            cashPriceBdt = 5600,
            originalPriceBdt = 6400,
            rating = 4.8,
            reviewCount = 205,
            imageRes = R.drawable.img_home_appliance,
            specsEn = listOf(
                "100% Copper Winding 750W High-Torque Motor",
                "3 Stainless Steel Jars (Wet, Dry, Chutney)",
                "Overload Thermal Protector & Anti-Skid Base",
                "1 Year Official Miyako Service Warranty"
            ),
            specsBn = listOf(
                "১০০% কপার মোটর ৭৫০ ওয়াট হাই-টর্ক ক্ষমতা",
                "৩টি স্টেইনলেস স্টিল জার (ওয়েট, ড্রাই, চাটনি)",
                "ওভারলোড থার্মাল প্রোটেক্টর ও অ্যান্টি-স্কিড বেস",
                "১ বছরের অফিশিয়াল মিয়াকো সার্ভিস ওয়ারেন্টি"
            ),
            maxTenure = InstallmentTenure.SIX_MONTHS,
            badgeEn = "Best Seller",
            badgeBn = "বেস্ট সেলার"
        ),
        Product(
            id = "rh-grocery-ramadan-starter",
            nameEn = "Rimvo Essentials Pantry & Breakfast Box",
            nameBn = "রিমভো এসেনশিয়াল প্যান্ট্রি ও ব্রেকফাস্ট বক্স",
            brand = "Rimvo Fresh",
            category = ProductCategory.GROCERY,
            cashPriceBdt = 2950,
            originalPriceBdt = 3300,
            rating = 4.7,
            reviewCount = 164,
            imageRes = R.drawable.img_grocery_combo,
            specsEn = listOf(
                "Atta 5kg + Chhola Boot 2kg + Sugar 2kg",
                "Pure Mustard Oil 1L + Full Cream Milk 1kg",
                "Dates (Mariam) 1kg + Vermicelli & Oats",
                "Freshly Packed & Sealed Quality Guarantee"
            ),
            specsBn = listOf(
                "আটা ৫ কেজি + ছোলা বুট ২ কেজি + চিনি ২ কেজি",
                "খাঁটি সরিষার তেল ১ লিটার + গুঁড়া দুধ ১ কেজি",
                "মরিয়ম খেজুর ১ কেজি + সেমাই ও ওটস",
                "সতেজ ও সিলড কোয়ালিটি গ্যারান্টি"
            ),
            maxTenure = InstallmentTenure.THREE_MONTHS,
            badgeEn = "0% 3M BNPL",
            badgeBn = "০% ৩ মাসের কিস্তি"
        ),
        Product(
            id = "vs-vision-32-tv",
            nameEn = "Vision 32\" HD Android Smart LED TV + Soundbar",
            nameBn = "ভিশন ৩২\" এইচডি অ্যান্ড্রয়েড স্মার্ট টিভি ও সাউন্ডবার",
            brand = "Vision",
            category = ProductCategory.ELECTRONICS,
            cashPriceBdt = 18500,
            originalPriceBdt = 21000,
            rating = 4.7,
            reviewCount = 112,
            imageRes = R.drawable.img_electronics_tv,
            specsEn = listOf(
                "A+ Grade IPS Panel with 178° Wide Viewing Angle",
                "Built-in Wi-Fi, YouTube & Chorki/Hoichoi Ready",
                "Includes 30W Bluetooth Compact Soundbar",
                "2 Years Vision Official Replacement Guarantee"
            ),
            specsBn = listOf(
                "এ+ গ্রেড আইপিএস প্যানেল ও ১৭৮° ওয়াইড ভিউ",
                "বিল্ট-ইন ওয়াই-ফাই, ইউটিউব ও চরকি/হইচই সাপোর্ট",
                "সাথে ৩০ ওয়াট ব্লুটুথ সাউন্ডবার ফ্রি",
                "২ বছরের ভিশন অফিশিয়াল রিপ্লেসমেন্ট গ্যারান্টি"
            ),
            maxTenure = InstallmentTenure.TWELVE_MONTHS,
            badgeEn = "Combo Deal",
            badgeBn = "কম্বো অফার"
        ),
        Product(
            id = "wl-walton-rice-cooker",
            nameEn = "Walton 2.8L Stainless Multi-Function Rice Cooker",
            nameBn = "ওয়ালটন ২.৮ লিটার স্টেইনলেস মাল্টি-ফাংশন রাইস কুকার",
            brand = "Walton",
            category = ProductCategory.APPLIANCES,
            cashPriceBdt = 3850,
            originalPriceBdt = 4300,
            rating = 4.6,
            reviewCount = 89,
            imageRes = R.drawable.img_home_appliance,
            specsEn = listOf(
                "Double Non-Stick & Stainless Inner Pot",
                "Automatic Keep-Warm & Steamer Tray Included",
                "1000W Fast Heating Element",
                "1 Year Walton Service Center Warranty"
            ),
            specsBn = listOf(
                "ডাবল নন-স্টিক ও স্টেইনলেস ইনার পট",
                "অটোমেটিক কিপ-ওয়ার্ম ও স্টিমার ট্রে",
                "১০০০ ওয়াট দ্রুত হিটিং এলিমেন্ট",
                "১ বছরের ওয়ালটন সার্ভিস সেন্টার ওয়ারেন্টি"
            ),
            maxTenure = InstallmentTenure.THREE_MONTHS,
            badgeEn = "0% 3M BNPL",
            badgeBn = "০% ৩ মাসের কিস্তি"
        )
    )

    val featuredBrands = listOf("Walton", "Vision", "Singer", "Miyako", "Rimvo Fresh")

    fun findById(id: String): Product? = products.firstOrNull { it.id == id }
}
