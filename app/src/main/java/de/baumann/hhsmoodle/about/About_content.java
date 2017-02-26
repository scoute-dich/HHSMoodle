package de.baumann.hhsmoodle.about;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.model.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.model.MaterialAboutTitleItem;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_intro;
import de.baumann.hhsmoodle.helper.helper_main;


class About_content {

    static MaterialAboutList createMaterialAboutList(final Context c) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .icon(R.mipmap.ic_launcher)
                .build());

        try {

            appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(c,
                    ContextCompat.getDrawable(c, R.drawable.school),
                    "Version",
                    false));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_changelog)
                .subText(R.string.about_changelog_summary)
                .icon(R.drawable.format_list_bulleted)
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(c, c.getString(R.string.about_changelog), "https://github.com/scoute-dich/HHSMoodle/blob/master/CHANGELOG.md", true, false))
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_license)
                .subText(R.string.about_license_summary)
                .icon(R.drawable.copyright)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        final AlertDialog d = new AlertDialog.Builder(c)
                                .setTitle(R.string.about_title)
                                .setMessage(helper_main.textSpannable(c.getString(R.string.about_text)))
                                .setPositiveButton(c.getString(R.string.toast_yes),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        }).show();
                        d.show();
                        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                    }
                })
                .build());


        appCardBuilder.addItem(ConvenienceBuilder.createEmailItem(c,
                ContextCompat.getDrawable(c, R.drawable.bug),
                c.getString(R.string.action_problem_summary),
                true,
                "juergen.baumann@huebsch.karlsruhe.de",
                "HHS Moodle"));

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_intro)
                .subText(R.string.about_intro_summary)
                .icon(R.drawable.information_outline)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(c, Activity_intro.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        c.startActivity(intent);
                    }
                })
                .build());


        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.about_title_dev);

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.about_dev)
                .subText(R.string.about_dev_summary)
                .icon(R.drawable.gaukler_faun)
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(c, c.getString(R.string.about_dev), "https://github.com/scoute-dich/", true, false))
                .build());

        MaterialAboutCard.Builder convenienceCardBuilder = new MaterialAboutCard.Builder();
        convenienceCardBuilder.title(R.string.about_title_libs);

        convenienceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Android Onboarder")
                .subText(R.string.about_license_3)
                .icon(R.drawable.github_circle)
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(c, "Android Onboarder", "https://github.com/chyrta/AndroidOnboarder", true, false))
                .build());

        convenienceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Encrypted Userprefs")
                .subText(R.string.about_license_5)
                .icon(R.drawable.github_circle)
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(c, "Encrypted Userprefs", "https://github.com/sveinungkb/encrypted-userprefs", true, false))
                .build());

        convenienceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Material About Library")
                .subText(R.string.about_license_7)
                .icon(R.drawable.github_circle)
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(c, "Material About Library", "https://github.com/daniel-stoneuk/material-about-library", true, false))
                .build());

        convenienceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Material Design Icons")
                .subText(R.string.about_license_8)
                .icon(R.drawable.github_circle)
                .setOnClickListener(ConvenienceBuilder.createWebViewDialogOnClickAction(c, "Material Design Icons", "https://github.com/Templarian/MaterialDesign", true, false))
                .build());

        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build(), convenienceCardBuilder.build());
    }

}
